package com.webank.wedatasphere.exchangis.job.server.execution.loadbalance;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.*;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.AbstractLoadBalanceSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.LoadBalanceSchedulerTask;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.SchedulerContext;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
import org.apache.linkis.scheduler.queue.SchedulerEventState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class FlexibleTenancyLoadBalancer extends AbstractTaskSchedulerLoadBalancer implements SchedulerThread {

    private static final Logger LOG = LoggerFactory.getLogger(FlexibleTenancyLoadBalancer.class);
    /**
     * key: {tenancy}_{schedulerTask_name}
     */
    private ConcurrentHashMap<String, SchedulerTaskContainer> tenancySchedulerTasks = new ConcurrentHashMap<>();

    static class Constraints{
        private static final CommonVars<Integer> SCHEDULE_INTERVAL = CommonVars.apply("wds.exchangis.job.task.scheduler.load-balancer.flexible.schedule-in-millisecond", 3000);
        private static final CommonVars<Double> SEGMENT_MAX_OCCUPY = CommonVars.apply("wds.exchangis.job.task.scheduler.load-balancer.flexible.segments.max-occupy", 0.35d);
        private static final CommonVars<Double> SEGMENT_MIN_OCCUPY = CommonVars.apply("wds.exchangis.job.task.scheduler.load-balancer.flexible.segments.min-occupy", 0.15d);
        private static final CommonVars<Integer> SEGMENT_ADJUST_STEP = CommonVars.apply("wds.exchangis.job.task.scheduler.load-balancer.flexible.segments.adjust-step", 5);
    }

    private boolean isShutdown = false;

    private Future<?> balanceFuture;

    public FlexibleTenancyLoadBalancer(Scheduler scheduler, TaskManager<LaunchedExchangisTask> taskManager) {
        super(scheduler, taskManager);
    }


    @Override
    protected LoadBalanceSchedulerTask<LaunchedExchangisTask> choose(LaunchedExchangisTask launchedExchangisTask, Class<?> schedulerTaskClass, boolean unchecked) {
        if( !unchecked  || isSuitableClass(schedulerTaskClass)){
            // Fetch the latest info
            launchedExchangisTask = getTaskManager().getRunningTask(launchedExchangisTask.getTaskId());
            // If the value is None means that the task is ended
            if (Objects.nonNull(launchedExchangisTask) && !TaskStatus.isCompleted(launchedExchangisTask.getStatus())) {
                // Use the exec user as tenancy
                String tenancy = launchedExchangisTask.getExecuteUser();
                GroupFactory groupFactory = getScheduler().getSchedulerContext().getOrCreateGroupFactory();
                if (groupFactory instanceof TenancyParallelGroupFactory &&
                        !((TenancyParallelGroupFactory) groupFactory).getTenancies().contains(tenancy)) {
                    // Unrecognized tenancy name
                    tenancy = "";
                }
                if (StringUtils.isBlank(tenancy)) {
                    tenancy = TenancyParallelGroupFactory.DEFAULT_TENANCY;
                }
                // Select one
                return geOrCreateSchedulerTaskContainer(tenancy, schedulerTaskClass).select();
            }

        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private LoadBalanceSchedulerTask<LaunchedExchangisTask> createLoadBalanceSchedulerTask(Class<?> schedulerTaskClass){
        Constructor<?>[] constructors = schedulerTaskClass.getDeclaredConstructors();
        if (constructors.length <= 0){
            throw new ExchangisTaskExecuteException.Runtime("Cannot find any constructors from load balance scheduler task: [" + schedulerTaskClass.getSimpleName() + "]", null);
        }
        // Use the first one constructor
        Constructor<?> constructor = constructors[0];
        Object[] parameters = new Object[constructor.getParameterCount()];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0 ;i < parameterTypes.length; i++){
            Class<?> parameterType = parameterTypes[i];
            if (parameterType.isAssignableFrom(TaskManager.class)){
                parameters[i] = getTaskManager();
            } else if (parameterType.isAssignableFrom(Scheduler.class)){
                parameters[i] = getScheduler();
            } else if (parameterType.isAssignableFrom(this.getClass())){
                parameters[i] = this;
            } else {
                parameters[i] = null;
            }
        }
        try {
            LoadBalanceSchedulerTask<LaunchedExchangisTask> loadBalanceSchedulerTask =  (LoadBalanceSchedulerTask<LaunchedExchangisTask>) constructor.newInstance(parameters);
            //Use the current timestamp as ID
            loadBalanceSchedulerTask.setId(String.valueOf(System.currentTimeMillis()));
            return loadBalanceSchedulerTask;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ExchangisTaskExecuteException.Runtime("Cannot new instance of load balance scheduler task: [" + schedulerTaskClass.getSimpleName() + "]", e);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Balancer-Thread" + getName());
        LOG.info("Thread:[ {} ] is started. ", Thread.currentThread().getName());
        initLoadBalancerSchedulerTasks();
        ConsumerManager consumerManager = getScheduler().getSchedulerContext().getOrCreateConsumerManager();
        Map<String, ExecutorService> tenancyExecutorServices = new HashMap<>();
        int residentThreads = 0;
        if (consumerManager instanceof TenancyParallelConsumerManager){
            tenancyExecutorServices = ((TenancyParallelConsumerManager) consumerManager).getTenancyExecutorServices();
            residentThreads = ((TenancyParallelConsumerManager) consumerManager).getInitResidentThreads();
        } else {
            LOG.warn("Cannot auto scale-in/out on the consumer manager: [" + consumerManager.getClass().getSimpleName() +"] which is not a tenancy consumer manager");
            isShutdown = true;
        }
        while (!isShutdown){
            try {
                loop(tenancyExecutorServices, residentThreads);
                Thread.sleep(Constraints.SCHEDULE_INTERVAL.getValue());
            } catch (Exception e) {
                if (e instanceof InterruptedException && isShutdown){
                    LOG.info("Receive the interrupt signal from shutdown operation");
                } else {
                    LOG.warn("Unknown exception in scale-in/out segments of load balance scheduler task", e);
                }
                try {
                    // Enforce to sleep
                    Thread.sleep(Constraints.SCHEDULE_INTERVAL.getValue());
                } catch (InterruptedException ex) {
                    //Ignore
                }
            }
        }
        LOG.info("Thread:[ {} ] is stopped. ", Thread.currentThread().getName());
    }


    @Override
    public void start() {
        if (Objects.isNull(this.scheduler)){
            throw new ExchangisTaskExecuteException.Runtime("TaskScheduler cannot be empty, please set it before starting the ["+ getName() +"]!", null);
        }
        if (Objects.nonNull(this.balanceFuture)){
            throw new ExchangisTaskExecuteException.Runtime("The load balancer: [" + getName() +"]  has been started before", null);
        }
        // Submit self to default executor service
        this.balanceFuture = this.scheduler.getSchedulerContext()
                .getOrCreateConsumerManager().getOrCreateExecutorService().submit(this);
    }

    /**
     * Loop method
     * @param tenancyExecutorServices executorServices
     */
    private void loop(Map<String, ExecutorService> tenancyExecutorServices, int residentThreads){
        LOG.trace("Start to auto scale-in/out segments of load balance scheduler task");
        int adjustStep = Constraints.SEGMENT_ADJUST_STEP.getValue();
        Map<String, LoopCounter> tenancyLoopCounter = new HashMap<>();
        this.tenancySchedulerTasks.forEach((key, taskContainer) -> tenancyLoopCounter.compute(taskContainer.tenancy, (tenancy, counter) -> {
            if (null == counter){
                counter = new LoopCounter();
            }
            counter.containers.incrementAndGet();
            counter.segments.addAndGet(taskContainer.segments.length);
            for (SchedulerTaskSegment segment : taskContainer.segments){
                counter.pollerSize.addAndGet(segment.loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().size());
            }
            counter.taskContainers.add(taskContainer);
            return counter;
        }));
        tenancyExecutorServices.forEach((tenancy, executorService) -> {
            LoopCounter loopCounter = tenancyLoopCounter.get(tenancy);
            if (Objects.nonNull(loopCounter)){
                if (loopCounter.pollerSize.get() > 0) {
                    LOG.info("Monitor: [tenancy: {}, task_segments: {}, wait_in_poll: {}]", tenancy, loopCounter.segments.get(),
                            loopCounter.pollerSize.get());
                }
                ThreadPoolExecutor pool = (ThreadPoolExecutor)executorService;
                int adjustSegmentNum = 0;
                int coreSize = pool.getCorePoolSize();
                // Must more than residentThreads
//                if (TenancyParallelGroupFactory.DEFAULT_TENANCY.equals(tenancy) || coreSize > residentThreads){
                    int segments = loopCounter.segments.get();
                    // TODO fix the problem that the value of residentThreads always equal 1 for not default consumer
                    int restSize = TenancyParallelGroupFactory.DEFAULT_TENANCY.equals(tenancy)? coreSize - residentThreads - 1: coreSize - 1;
                    if (restSize > 0) {
                        int activeThreads = pool.getActiveCount();
                        if (activeThreads >= coreSize) {
                            // All threads is active, should reduce the number of segments
                            adjustSegmentNum = Math.min((int) Math.floor((double) restSize * Constraints.SEGMENT_MIN_OCCUPY.getValue()), segments);
                        } else {
                            adjustSegmentNum = Math.max((int) Math.floor((double) restSize * Constraints.SEGMENT_MAX_OCCUPY.getValue()), segments);
                        }
                        adjustSegmentNum = adjustSegmentNum > segments ? segments + Math.min(adjustStep, adjustSegmentNum - segments)
                                : segments - Math.min(adjustStep, segments - adjustSegmentNum);
                        if (segments != adjustSegmentNum) {
                            // Div the number of container
                            int average = adjustSegmentNum / loopCounter.containers.get();
                            LOG.info("Adjust total number of load balance scheduler task segments for tenancy: [{}] from {} to {}, average {}",
                                    tenancy, segments, adjustSegmentNum, average);
                            for (int i = 0; i < loopCounter.containers.get(); i++) {
                                if (i == loopCounter.containers.get() - 1) {
                                    loopCounter.taskContainers.get(i).adjustSegment(adjustSegmentNum);
                                } else {
                                    loopCounter.taskContainers.get(i).adjustSegment(average);
                                    adjustSegmentNum = adjustSegmentNum - average;
                                }
                            }
                        }
                    }
//                }
            }
        });
        LOG.trace("End to auto scale-in/out segments of load balance scheduler task");
    }
    @Override
    public void stop() {
        if (Objects.nonNull(this.balanceFuture)){
            this.isShutdown = true;
            this.balanceFuture.cancel(true);
            this.tenancySchedulerTasks.forEach((tenancy, container) -> {
                container.segmentLock.writeLock().lock();
                try{
                    for(SchedulerTaskSegment segment : container.segments){
                        if (segment.loadBalanceSchedulerTask instanceof AbstractExchangisSchedulerTask){
                            ((AbstractExchangisSchedulerTask) segment.loadBalanceSchedulerTask).kill();
                        }
                    }
                }finally {
                    container.segmentLock.writeLock().unlock();
                }
            });
            this.tenancySchedulerTasks.clear();
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Get or create scheduler task container
     * @return container
     */
    private SchedulerTaskContainer geOrCreateSchedulerTaskContainer(String tenancy, Class<?> schedulerTaskClass){
        String schedulerTaskName = schedulerTaskClass.getSimpleName();
        return tenancySchedulerTasks.compute(tenancy + "_" + schedulerTaskName,(key, taskContainer) -> {
            if (Objects.isNull(taskContainer)){
                LoadBalanceSchedulerTask<LaunchedExchangisTask> headSchedulerTask = createLoadBalanceSchedulerTask(schedulerTaskClass);
                if (headSchedulerTask instanceof AbstractLoadBalanceSchedulerTask){
                    ((AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask>) headSchedulerTask)
                            .setSchedulerLoadBalancer(FlexibleTenancyLoadBalancer.this);
                }
                headSchedulerTask.setTenancy(tenancy);
                try {
                    getScheduler().submit(headSchedulerTask);
                } catch (Exception e){
                    // Only if not enough reserved threads in scheduler
                    throw new ExchangisTaskExecuteException.Runtime("If there is no enough reserved threads in scheduler for tenancy: [" + tenancy
                            + "], load balance scheduler task: [" + schedulerTaskName + "]? please invoke setInitResidentThreads(num) method in consumerManager", e);
                }
                taskContainer = new SchedulerTaskContainer(headSchedulerTask);
                taskContainer.tenancy = tenancy;
                LOG.info("Create scheduler task container[ tenancy: {}, load balance scheduler task: {} ]", tenancy, schedulerTaskName);
            }
            return taskContainer;
        });
    }

    /**
     * Init to pre create task container for load balancer scheduler tasks
     */
    private void initLoadBalancerSchedulerTasks(){
        SchedulerContext schedulerContext = getScheduler().getSchedulerContext();
        if (schedulerContext instanceof ExchangisSchedulerContext){
            Optional.ofNullable(((ExchangisSchedulerContext)schedulerContext).getTenancies()).ifPresent(tenancies -> {
                tenancies.forEach(tenancy -> {
                    // Skip the system tenancy
                    if (!tenancy.startsWith(".")) {
                        for (Class<?> registeredTaskClass : registeredTaskClasses) {
                            geOrCreateSchedulerTaskContainer(tenancy, registeredTaskClass);
                        }
                    }
                });
            });
            // init scheduler task container for default tenancy
            for (Class<?> registeredTaskClass : registeredTaskClasses) {
                geOrCreateSchedulerTaskContainer(TenancyParallelGroupFactory.DEFAULT_TENANCY, registeredTaskClass);
            }
        }
    }
    static class LoopCounter {

        AtomicInteger containers = new AtomicInteger(0);

        AtomicInteger segments = new AtomicInteger(0);

        AtomicInteger pollerSize = new AtomicInteger(0);

        List<SchedulerTaskContainer> taskContainers = new ArrayList<>();
    }

    /**
     * Scheduler
     */
    private class SchedulerTaskContainer{

        String tenancy;

        String taskName;

        SchedulerTaskSegment[] segments;

        ReentrantReadWriteLock segmentLock = new ReentrantReadWriteLock();

        SchedulerTaskContainer(LoadBalanceSchedulerTask<LaunchedExchangisTask> schedulerTask){
            // TODO should create the strategy of defining 'weight' value
            segments = new SchedulerTaskSegment[]{new SchedulerTaskSegment(1, schedulerTask)};
            taskName = schedulerTask.getClass().getSimpleName();
        }
        LoadBalanceSchedulerTask<LaunchedExchangisTask> select(){
            segmentLock.writeLock().lock();
            try {
                int segmentIndex = selectSegment(segments);
                SchedulerTaskSegment segment = segments[segmentIndex];
                segment.cwt = segment.cwt - 1;
                return segment.loadBalanceSchedulerTask;
            }finally {
                segmentLock.writeLock().unlock();
            }
        }

        private void adjustSegment(int adjustNum){
            if (adjustNum != segments.length) {
                segmentLock.writeLock().lock();
                try {
                    if (adjustNum > segments.length) {
                        scaleInSegment(adjustNum - segments.length);
                    } else {
                        scaleOutSegment(segments.length - adjustNum);
                    }
                }finally {
                    segmentLock.writeLock().unlock();
                }
            }
        }

        /**
         * Scale-out segment
         * @param scaleOut
         */
        private void scaleOutSegment(int scaleOut){
            int newSize = segments.length - scaleOut;
            LOG.info("Scale-out segments for tenancy: [{}]，scaleOut: [{}], newSize: [{}], scheduler_task_type: [{}]",
                    tenancy, scaleOut, newSize, taskName);
            if (newSize <= 0){
                LOG.warn("Scale-out fail, the newSize cannot <= 0");
                return;
            }
            SchedulerTaskSegment[] newSegments = new SchedulerTaskSegment[newSize];
            System.arraycopy(segments, 0, newSegments, 0, newSize);
            int offset = -1;
            Map<String, List<LoadBalanceSchedulerTask<LaunchedExchangisTask>>> waitForCombine = new HashMap<>();
            for(int i = newSize; i < segments.length; i ++){
                LoadBalanceSchedulerTask<LaunchedExchangisTask> schedulerTask = segments[i].loadBalanceSchedulerTask;
                try {
                    SchedulerTaskSegment newSegment = null;
                    int count = 0;
                    do {
                        offset = (offset + 1) % newSize;
                        newSegment = newSegments[offset];
                        count ++;
                    }while (newSegment.loadBalanceSchedulerTask.getState() != SchedulerEventState.Running() && count <= newSize);
                    if (offset != 0 && newSegment.loadBalanceSchedulerTask.getState() != SchedulerEventState.Running()){
                        // Ignore the first load balance scheduler task
                        LOG.error("Unable to scale-out segments for tenancy: [{}], reason:" +
                                " the scheduler task has still in state[{}], scheduler_task_type: [{}], offset: [{}]",
                                tenancy, newSegment.loadBalanceSchedulerTask.getState(), taskName, offset);
                        return;
                    }
                    waitForCombine.compute(offset + "", (key, value) -> {
                        if (Objects.isNull(value)){
                            value = new ArrayList<>();
                        }
                        value.add(schedulerTask);
                        return value;
                    });
                } catch (Exception e){
                    LOG.warn("Scale-out segments for tenancy: [{}] wrong, index: [{}], scheduler_task_type: [{}]", tenancy, i, taskName, e);
                }
            }
            // Kill all
            waitForCombine.forEach((key, tasks) -> {
                SchedulerTaskSegment newSegment = newSegments[Integer.parseInt(key)];
                tasks.forEach(task -> {
                    // Kill task
                    if (AbstractExchangisSchedulerTask.class.isAssignableFrom(task.getClass())) {
                        ((AbstractExchangisSchedulerTask) task).kill();
                    }
                    // Merge/Combine the poller
                    LoadBalancePoller<LaunchedExchangisTask> poller = task.getOrCreateLoadBalancePoller();
                    LOG.info("Merge/combine [{}] poller form {} to {}", taskName, task.getId(), newSegment.loadBalanceSchedulerTask.getId());
                    newSegment.loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().combine(poller);
                });
            });
            segments = newSegments;
        }
        /**
         * Scale-in segment
         * @param scaleIn
         */
        private void scaleInSegment(int scaleIn){
            LOG.info("Scale-in segments for tenancy: [{}]，scaleIn: [{}], newSize: [{}], scheduler task: [{}]",
                    tenancy, scaleIn, segments.length + scaleIn, taskName);
            SchedulerTaskSegment[] newSegments = new SchedulerTaskSegment[segments.length + scaleIn];
            System.arraycopy(segments, 0, newSegments, 0, segments.length);
            for(int i = segments.length; i < segments.length + scaleIn; i ++){
                try {
                    LoadBalanceSchedulerTask<LaunchedExchangisTask> schedulerTask =
                            createLoadBalanceSchedulerTask(segments[0].loadBalanceSchedulerTask.getClass());
                    //
                    final SchedulerTaskSegment segment = new SchedulerTaskSegment(0, schedulerTask);
                    if (schedulerTask instanceof AbstractLoadBalanceSchedulerTask){
                        ((AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask>) schedulerTask)
                                .setSchedulerLoadBalancer(FlexibleTenancyLoadBalancer.this);
                        ((AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask>) schedulerTask).setScheduleListener( task -> {
                            segmentLock.writeLock().lock();
                            try{
                                segment.setWeight(1);
                                LOG.info("Init the weight of segment to 1, relate scheduler task: {}", task.getName());
                            }finally {
                                segmentLock.writeLock().unlock();
                            }
                        });
                    }
                    schedulerTask.setTenancy(tenancy);
                    newSegments[i] = segment;
                    getScheduler().submit(schedulerTask);
                } catch (Exception e){
                    LOG.warn("Scale-in segments for tenancy: [{}] wrong, index: [{}]", tenancy, i, e);
                }
            }
            segments = newSegments;
        }
        /**
         * Select segment
         * @param segments segments
         * @return index
         */
        private int selectSegment(SchedulerTaskSegment[] segments) {
            int u = 0;
            int reset = -1;
            while (true) {
                for (int i = 0; i < segments.length; i++) {
                    if (null == segments[i] || segments[i].cwt <= 0) {
                        continue;
                    }
                    u = i;
                    while (i < segments.length - 1) {
                        i++;
                        if (null == segments[i] || segments[i].cwt <= 0) {
                            continue;
                        }
                        if ((segments[u].wt * 1000 / segments[i].wt <
                                segments[u].cwt * 1000 / segments[i].cwt)) {
                            return u;
                        }
                        u = i;
                    }
                    return u;
                }
                if (reset++ > 0) {
                    return 0;
                }
                for (SchedulerTaskSegment segment : segments) {
                    segment.cwt = segment.wt;
                }
            }
        }
    }

    /**
     * Each segment has
     * weight => initial weight size
     * cWeight => current weight
     */
    private static class SchedulerTaskSegment{

        int wt = -1;

        int cwt = -1;

        String schedulerId;

        LoadBalanceSchedulerTask<LaunchedExchangisTask> loadBalanceSchedulerTask;

        SchedulerTaskSegment(int weight, LoadBalanceSchedulerTask<LaunchedExchangisTask> task){
            this.wt = weight;
            this.cwt = this.wt;
            this.loadBalanceSchedulerTask = task;
            this.schedulerId = task.getId();
        }

        public void setWeight(int weight){
            this.wt = weight;
            this.cwt = this.wt;
        }
    }

}
