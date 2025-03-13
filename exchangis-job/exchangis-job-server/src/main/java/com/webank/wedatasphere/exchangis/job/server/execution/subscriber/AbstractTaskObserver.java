package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Contains the schedule and publish strategies
 */
public abstract class AbstractTaskObserver<T  extends ExchangisTask> implements TaskObserver<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskObserver.class);

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL = 10000;

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_BATCH = 50;

    private static final int MAX_SUBSCRIBE_TIMES = 3;

    /**
     * Observer publish interval
     */
    protected static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_INTERVAL = CommonVars.apply("wds.exchangis.job.task.observer.publish.interval-in-mills", DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL);

    /**
     * Observer publish batch
     */
    protected static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_BATCH = CommonVars.apply("wds.exchangis.job.task.observer.publish.batch", DEFAULT_TASK_OBSERVER_PUBLISH_BATCH);


    private Scheduler scheduler;

    private TaskChooseRuler<T> taskChooseRuler;


    /**
     * Task manager
     */
    private TaskManager<T> taskManager;

    private TaskExecution<T> taskExecution;

    private ReentrantLock observerLock = new ReentrantLock();

    private Condition emptyCondition = observerLock.newCondition();

    private AtomicBoolean waitStatus = new AtomicBoolean(false);

    private Future<?> observerFuture;

    protected int publishBatch;

    protected int publishInterval;

    protected long lastPublishTime = -1;

    protected boolean isShutdown = false;

    protected TaskObserverService observerService;

    public AbstractTaskObserver(int publishBatch, int publishInterval){
        if (publishBatch <= 0){
            throw new IllegalArgumentException("Batch size of task subscribed cannot be less than(<) 0");
        }
        this.publishBatch = publishBatch;
        this.publishInterval = publishInterval;
    }

    public AbstractTaskObserver(){
        this.publishBatch = TASK_OBSERVER_PUBLISH_BATCH.getValue();
        this.publishInterval = TASK_OBSERVER_PUBLISH_INTERVAL.getValue();
        this.lastPublishTime = -1;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Observer-" + getName());
        LOG.info("Thread: [ {} ] is started. ", Thread.currentThread().getName());
        String instance = getInstance();
        while (!isShutdown) {
            try {
                List<T> publishedTasks;
                try {
                    publishedTasks = onPublish(instance, publishBatch);
                    if (Objects.isNull(publishedTasks)){
                        Thread.sleep(publishInterval);
                        continue;
                    }
                    // If list of publish tasks is not empty
                    if (publishedTasks.size() > 0 || this.lastPublishTime <= 0) {
                        this.lastPublishTime = System.currentTimeMillis();
                    }
                } catch (ExchangisTaskObserverException e){
                    e.setMethodName("call_on_publish");
                    throw e;
                }
                // Record the published size
                int publishedSize = publishedTasks.size();
                for ( int i = 0; i < MAX_SUBSCRIBE_TIMES && !publishedTasks.isEmpty(); i ++) {
                    List<T> chooseTasks;
                    TaskChooseRuler<T> chooseRuler = getTaskChooseRuler();
                    if (Objects.nonNull(chooseRuler)) {
                        try {
                            chooseTasks = choose(publishedTasks, getTaskChooseRuler(), getScheduler());
                        } catch (Exception e) {
                            throw new ExchangisTaskObserverException("call_choose_rule", "Fail to choose candidate tasks", e);
                        }
                        // The rest one
                        publishedTasks.removeAll(chooseTasks);
                    } else {
                        chooseTasks = new ArrayList<>(publishedTasks);
                        publishedTasks.clear();
                    }
                    if (!chooseTasks.isEmpty()) {
                        try {
                            subscribe(chooseTasks);
                        } catch (ExchangisTaskObserverException e) {
                            e.setMethodName("call_subscribe");
                            throw e;
                        }
                    }
                }
                // Discard the unsubscribed tasks
                discard(publishedTasks);
                sleepOrWaitIfNeed(publishedSize);
            } catch (Exception e){
                if(e instanceof ExchangisTaskObserverException){
                    LOG.warn("Observer exception in progress paragraph: [{}]",((ExchangisTaskObserverException)e).getMethodName(), e);
                }
                LOG.warn("Unknown exception happened in observing thread", e);
                // Enforce to sleep
                try {
                    Thread.sleep(publishInterval);
                } catch (InterruptedException ex) {
                    //Ignore
                }
            }
        }
        LOG.info("Thread: [ {} ] is stopped. ", Thread.currentThread().getName());
    }

    @Override
    public synchronized void start() {
        if (Objects.isNull(this.scheduler)){
            throw new ExchangisTaskObserverException.Runtime("TaskScheduler cannot be empty, please set it before starting the ["+ getName() +"]!", null);
        }
        if (Objects.nonNull(this.observerFuture)){
            throw new ExchangisTaskObserverException.Runtime("The observer: [" + getName() +"]  has been started before", null);
        }
        // Submit self to default executor service
        this.observerFuture = this.scheduler.getSchedulerContext()
                .getOrCreateConsumerManager().getOrCreateExecutorService().submit(this);
    }

    @Override
    public synchronized void stop() {
        if (Objects.nonNull(this.observerFuture)) {
            this.isShutdown = true;
            this.observerFuture.cancel(true);
        }
    }

    /**
     * Sleep or wait during the publishing and subscribe
     */
    private void sleepOrWaitIfNeed(int publishedSize){
        long observerWait = this.lastPublishTime + publishInterval - System.currentTimeMillis();
        if (!nextPublish(publishedSize, observerWait)) {
            observerWait = observerWait > 0? observerWait : publishInterval;
            boolean hasLock = observerLock.tryLock();
            if (hasLock) {
                try {
                    LOG.trace("TaskObserver:[{}] wait in {} ms to ", getName(), observerWait);
                    waitStatus.set(true);
                    emptyCondition.await(observerWait, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    if (isShutdown){
                        LOG.warn("TaskObserver:[{}] wait is interrupted by shutdown",getName());
                    } else {
                        LOG.warn("TaskObserver:[{}] wait is interrupted", getName(), e);
                    }
                } finally {
                    waitStatus.set(false);
                    observerLock.unlock();
                }
            }
        } else {
            LockSupport.parkNanos(1L);
        }
    }

    /**
     * If go on publish, else wait for the next interval
     * @param publishedSize published
     * @param observerWait wait time
     * @return bool
     */
    protected boolean nextPublish(int publishedSize, long observerWait){
        return publishedSize > 0 && observerWait <= 0;
    }

    protected abstract List<T> onPublish(String instance, int batchSize) throws ExchangisTaskObserverException;

    /**
     * Call publish
     */
    protected void publish(){
        if (waitStatus.get()) {
            observerLock.lock();
            try {
                emptyCondition.signalAll();
            } finally {
                observerLock.unlock();
            }
        }
    }
    protected List<T> choose(List<T> candidateTasks, TaskChooseRuler<T> chooseRuler, Scheduler scheduler){
        return chooseRuler.choose(candidateTasks, scheduler);
    }

    @Override
    public TaskChooseRuler<T> getTaskChooseRuler() {
        return this.taskChooseRuler;
    }

    @Override
    public void setTaskChooseRuler(TaskChooseRuler<T> ruler) {
        this.taskChooseRuler = ruler;
    }


    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public TaskManager<T> getTaskManager() {
        return this.taskManager;
    }

    @Override
    public void setTaskManager(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void setTaskExecution(TaskExecution<T> taskExecution) {
        this.taskExecution = taskExecution;
    }

    @Override
    public TaskExecution<T> getTaskExecution() {
        return taskExecution;
    }

    @Override
    public void setTaskObserverService(TaskObserverService observerService) {
        this.observerService = observerService;
    }

    @Override
    public TaskObserverService getTaskObserverService() {
        return this.observerService;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
