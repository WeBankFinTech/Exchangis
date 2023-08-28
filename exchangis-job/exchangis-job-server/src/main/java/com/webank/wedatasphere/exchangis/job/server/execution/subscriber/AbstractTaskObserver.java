package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Contains the schedule and publish strategies
 */
public abstract class AbstractTaskObserver<T  extends ExchangisTask> implements TaskObserver<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskObserver.class);

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL = 10000;

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_BATCH = 50;

    private static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_INTERVAL = CommonVars.apply("wds.exchangis.job.task.observer.publish.interval-in-millisecond", DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL);

    private static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_BATCH = CommonVars.apply("wds.exchangis.job.task.observer.publish.batch", DEFAULT_TASK_OBSERVER_PUBLISH_BATCH);

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

    private boolean isShutdown = false;

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
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Observe-Thread-" + getName());
        LOG.info("Thread: [ {} ] is started. ", Thread.currentThread().getName());
        this.lastPublishTime = System.currentTimeMillis();
        while (!isShutdown) {
            try {
                List<T> publishedTasks;
                try {
                    publishedTasks = onPublish(publishBatch);
                    // If list of publish tasks is not empty
                    if (publishedTasks.size() > 0) {
                        this.lastPublishTime = System.currentTimeMillis();
                    }
                } catch (ExchangisTaskObserverException e){
                    e.setMethodName("call_on_publish");
                    throw e;
                }
                if (!publishedTasks.isEmpty()) {
                    List<T> chooseTasks;
                    try {
                        chooseTasks = choose(publishedTasks, getTaskChooseRuler(), getScheduler());
                    } catch (Exception e){
                        throw new ExchangisTaskObserverException("call_choose_rule", "Fail to choose candidate tasks", e);
                    }
                    if (!chooseTasks.isEmpty()) {
                        try {
                            subscribe(chooseTasks);
                        } catch (ExchangisTaskObserverException e){
                            e.setMethodName("call_subscribe");
                            throw e;
                        }
                    }
                }
                sleepOrWaitIfNeed(publishedTasks);
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
     * Sleep or wait during the publish and subscribe
     * @param publishedTasks published tasks
     */
    private void sleepOrWaitIfNeed(List<T> publishedTasks){
        long observerWait = this.lastPublishTime + publishInterval - System.currentTimeMillis();
        if (publishedTasks.isEmpty() || observerWait > 0) {
            observerWait = observerWait > 0? observerWait : publishInterval;
            boolean hasLock = observerLock.tryLock();
            if (hasLock) {
                try {
                    LOG.trace("TaskObserver wait in {} ms to ", observerWait);
                    waitStatus.set(true);
                    emptyCondition.await(observerWait, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    if (isShutdown){
                        LOG.warn("TaskObserver wait is interrupted by shutdown");
                    } else {
                        LOG.warn("TaskObserver wait is interrupted", e);
                    }
                } finally {
                    waitStatus.set(false);
                    observerLock.unlock();
                }
            }
        }
    }
    protected abstract List<T> onPublish(int batchSize) throws ExchangisTaskObserverException;

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
    public String getName() {
        return getClass().getSimpleName();
    }
}
