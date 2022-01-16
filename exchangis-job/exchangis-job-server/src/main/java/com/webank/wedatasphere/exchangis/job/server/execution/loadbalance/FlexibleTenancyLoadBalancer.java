package com.webank.wedatasphere.exchangis.job.server.execution.loadbalance;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.DefaultTaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecutionListener;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskExecutionEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.*;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.LoadBalanceSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.MetricUpdateSchedulerTask;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
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
            String schedulerTaskName = schedulerTaskClass.getSimpleName();
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
                String finalTenancy = tenancy;
                SchedulerTaskContainer schedulerTaskContainer =tenancySchedulerTasks.compute(tenancy + "_" + schedulerTaskName,(key, taskContainer) -> {
                    if (Objects.isNull(taskContainer)){
                        LoadBalanceSchedulerTask<LaunchedExchangisTask> headSchedulerTask = createLoadBalanceSchedulerTask(schedulerTaskClass);
                        headSchedulerTask.setTenancy(finalTenancy);
                        try {
                            getScheduler().submit(headSchedulerTask);
                        } catch (Exception e){
                            // Only if not enough reserved threads in scheduler
                            throw new ExchangisTaskExecuteException.Runtime("If there is no enough reserved threads in scheduler for tenancy: [" + finalTenancy
                                    + "], load balance scheduler task: [" + schedulerTaskName + "]? please invoke setInitResidentThreads(num) method in consumerManager", e);
                        }
                        taskContainer = new SchedulerTaskContainer(headSchedulerTask);
                        taskContainer.tenancy = finalTenancy;
                    }
                    return taskContainer;
                });
                // Select one
                return schedulerTaskContainer.select();
            }

        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private LoadBalanceSchedulerTask<LaunchedExchangisTask> createLoadBalanceSchedulerTask(Class<?> schedulerTaskClass){
        Constructor<?>[] constructors = schedulerTaskClass.getDeclaredConstructors();
        if (constructors.length <= 0){
            throw new ExchangisTaskExecuteException.Runtime("Cannot find any constructors from load balance scheduler task: " + schedulerTaskClass.getSimpleName(), null);
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
            return (LoadBalanceSchedulerTask<LaunchedExchangisTask>) constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ExchangisTaskExecuteException.Runtime("Cannot new instance of load balance scheduler task: [" + schedulerTaskClass.getSimpleName() + "]", e);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Balancer-Thread" + getName());
        LOG.info("Thread:[ {} ] is started. ", Thread.currentThread().getName());
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
            counter.taskContainers.add(taskContainer);
            return counter;
        }));
        tenancyExecutorServices.forEach((tenancy, executorService) -> {
            LoopCounter loopCounter = tenancyLoopCounter.get(tenancy);
            if (Objects.nonNull(loopCounter)){
                ThreadPoolExecutor pool = (ThreadPoolExecutor)executorService;
                int adjustSegmentNum = 0;
                int coreSize = pool.getCorePoolSize();
                // Must more than residentThreads
                if (coreSize > residentThreads){
                    int segments = loopCounter.segments.get();
                    int restSize = coreSize - residentThreads;
                    int activeThreads = pool.getActiveCount();
                    if (activeThreads >= coreSize){
                        // All threads is active, should reduce the number of segments
                        adjustSegmentNum = Math.min((int) Math.floor((double)restSize * Constraints.SEGMENT_MIN_OCCUPY.getValue()), segments);
                    } else {
                        adjustSegmentNum = Math.max((int) Math.floor((double)restSize * Constraints.SEGMENT_MAX_OCCUPY.getValue()), segments);
                    }
                    adjustSegmentNum = adjustSegmentNum > segments ? segments + Math.min(adjustStep, adjustSegmentNum - segments)
                            : segments - Math.min(adjustStep, segments - adjustSegmentNum);
                    if (segments != adjustSegmentNum) {
                        // Div the number of container
                        int average = adjustSegmentNum / loopCounter.containers.get();
                        LOG.info("Adjust total number of load balance scheduler task segments for tenancy: [{}] to [{}], average [{}]",
                                tenancy, adjustSegmentNum, average);
                        for (int i = 0; i < loopCounter.containers.get(); i++) {
                            if (adjustSegmentNum < average) {
                                loopCounter.taskContainers.get(i).adjustSegment(adjustSegmentNum);
                            } else {
                                loopCounter.taskContainers.get(i).adjustSegment(average);
                                adjustSegmentNum = adjustSegmentNum - average;
                            }
                        }
                    }
                }
            }
        });
        LOG.trace("Start to auto scale-in/out segments of load balance scheduler task");
    }
    @Override
    public void stop() {
        if (Objects.nonNull(this.balanceFuture)){
            this.isShutdown = true;
            this.balanceFuture.cancel(true);
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    static class LoopCounter {

        AtomicInteger containers = new AtomicInteger(0);

        AtomicInteger segments = new AtomicInteger(0);

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
            LOG.info("Scale-out segments for tenancy: [{}]，decr_segment_size: [{}], scheduler task: [{}]", tenancy, scaleOut, taskName);
            int newSize = segments.length - scaleOut;
            SchedulerTaskSegment[] newSegments = new SchedulerTaskSegment[newSize];
            System.arraycopy(segments, 0, newSegments, 0, newSize);
            int offset = 0;
            for(int i = newSize; i < segments.length; i ++){
                LoadBalanceSchedulerTask<LaunchedExchangisTask> schedulerTask = segments[i].loadBalanceSchedulerTask;
                try {
                    if (AbstractExchangisSchedulerTask.class.isAssignableFrom(schedulerTask.getClass())) {
                        ((AbstractExchangisSchedulerTask) schedulerTask).kill();
                    }
                    // Merge the poller
                    LoadBalancePoller<LaunchedExchangisTask> poller = schedulerTask.getOrCreateLoadBalancePoller();
                    // Combine the poller
                    newSegments[offset % newSize].loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().combine(poller);
                } catch (Exception e){
                    LOG.warn("Scale-out segments for tenancy: [{}] wrong, index: [{}], scheduler task: [{}]", tenancy, i, taskName, e);
                }
            }
            segments = newSegments;
        }
        /**
         * Scale-in segment
         * @param scaleIn
         */
        private void scaleInSegment(int scaleIn){
            LOG.info("Scale-in segments for tenancy: [{}]，incr_segment_size: [{}], scheduler task: [{}]", tenancy, scaleIn, taskName);
            SchedulerTaskSegment[] newSegments = new SchedulerTaskSegment[segments.length + scaleIn];
            System.arraycopy(segments, 0, newSegments, 0, segments.length);
            for(int i = segments.length; i < segments.length + scaleIn; i ++){
                try {
                    LoadBalanceSchedulerTask<LaunchedExchangisTask> schedulerTask =
                            createLoadBalanceSchedulerTask(segments[0].loadBalanceSchedulerTask.getClass());
                    getScheduler().submit(schedulerTask);
                    segments[i] = new SchedulerTaskSegment(1, schedulerTask);
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
    }

    public static void main(String[] args){
        FlexibleTenancyLoadBalancer flexibleTenancyLoadBalancer = new FlexibleTenancyLoadBalancer(new ExchangisGenericScheduler(new ExchangisSchedulerExecutorManager(), new TenancyParallelConsumerManager()), new DefaultTaskManager(new TaskExecutionListener() {
            @Override
            public void onEvent(TaskExecutionEvent taskExecutionEvent) throws ExchangisOnEventException {
                System.out.println("hello world");
            }
        }));
        flexibleTenancyLoadBalancer.createLoadBalanceSchedulerTask(MetricUpdateSchedulerTask.class);
    }
}
