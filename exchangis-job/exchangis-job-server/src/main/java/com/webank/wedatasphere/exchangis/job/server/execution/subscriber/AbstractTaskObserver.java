package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
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
public abstract class AbstractTaskObserver implements TaskObserver<LaunchableExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskObserver.class);

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL = 5000;

    private static final int DEFAULT_TASK_OBSERVER_PUBLISH_BATCH = 50;

    private static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_INTERVAL = CommonVars.apply("wds.exchangis.job.observer.publish.interval-in-millisecond", DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL);

    private static final CommonVars<Integer> TASK_OBSERVER_PUBLISH_BATCH = CommonVars.apply("wds.exchangis.job.observer.publish.batch", DEFAULT_TASK_OBSERVER_PUBLISH_BATCH);
    @Resource
    private TaskScheduler taskScheduler;

    @Resource
    private TaskChooseRuler taskChooseRuler;

    /**
     * Task manager
     */
    @Resource
    private TaskManager taskManager;

    @Resource
    private TaskExecution taskExecution;

    private ReentrantLock observerLock = new ReentrantLock();

    private Condition emptyCondition = observerLock.newCondition();

    private AtomicBoolean waitStatus = new AtomicBoolean(false);

    private Future<?> observerFuture;

    protected int publishBatch = DEFAULT_TASK_OBSERVER_PUBLISH_BATCH;

    protected int publishInterval = DEFAULT_TASK_OBSERVER_PUBLISH_INTERVAL;

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
        while (!isShutdown) {
            try {
                List<LaunchableExchangisTask> publishedTasks;
                try {
                    publishedTasks = onPublish(publishBatch);
                    this.lastPublishTime = System.currentTimeMillis();
                } catch (ExchangisTaskObserverException e){
                    e.setMethodName("call_on_publish");
                    throw e;
                }
                if (!publishedTasks.isEmpty()) {
                    List<LaunchableExchangisTask> chooseTasks;
                    try {
                        chooseTasks = choose(publishedTasks, getTaskChooseRuler(), getTaskScheduler());
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
            }
        }
        LOG.info("Thread: [ {} ] is stopped. ", Thread.currentThread().getName());
    }

    @Override
    public synchronized void start() {
        if (Objects.isNull(this.taskScheduler)){
            throw new ExchangisTaskObserverException.Runtime("TaskScheduler cannot be empty, please set it before starting the ["+ getName() +"]!", null);
        }
        if (Objects.nonNull(this.observerFuture)){
            throw new ExchangisTaskObserverException.Runtime("The observer: [" + getName() +"]  has been started before", null);
        }
        // Submit self to default executor service
        this.observerFuture = this.taskScheduler.getSchedulerContext()
                .getOrCreateConsumerManager().getOrCreateExecutorService().submit(this);
    }

    @Override
    public synchronized void stop() {
        if (Objects.nonNull(this.observerFuture)) {
            this.observerFuture.cancel(true);
            this.isShutdown = true;
        }
    }

    /**
     * Sleep or wait during the publish and subscribe
     * @param publishedTasks published tasks
     */
    private void sleepOrWaitIfNeed(List<LaunchableExchangisTask> publishedTasks){
        long observerWait = this.lastPublishTime + publishInterval - System.currentTimeMillis();
        if (publishedTasks.isEmpty() || observerWait > 0) {
            observerWait = observerWait > 0? observerWait : publishBatch;
            boolean hasLock = observerLock.tryLock();
            if (hasLock) {
                try {
                    LOG.trace("TaskObserver wait in {} ms to ", observerWait);
                    waitStatus.set(true);
                    emptyCondition.await(observerWait, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    LOG.warn("TaskObserver wait is interrupted", e);
                } finally {
                    waitStatus.set(false);
                    observerLock.unlock();
                }
            }
        }
    }
    protected abstract List<LaunchableExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException;

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
    protected List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidateTasks,
                                                            TaskChooseRuler chooseRuler, TaskScheduler taskScheduler){
        return chooseRuler.choose(candidateTasks, taskScheduler);
    }

    @Override
    public TaskChooseRuler getTaskChooseRuler() {
        return this.taskChooseRuler;
    }

    @Override
    public void setTaskChooseRuler(TaskChooseRuler ruler) {
        this.taskChooseRuler = ruler;
    }

    @Override
    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    @Override
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public TaskExecution getTaskExecution() {
        return taskExecution;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
