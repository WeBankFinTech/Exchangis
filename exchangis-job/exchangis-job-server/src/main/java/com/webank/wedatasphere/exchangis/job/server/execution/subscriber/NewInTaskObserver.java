package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.SubmitSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelManager;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Subscribe the new task from database and then submit to scheduler
 */
@Component
public class NewInTaskObserver extends CacheInTaskObserver<LaunchableExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(NewInTaskObserver.class);


    @Resource
    private TaskParallelManager parallelManager;

    @Resource
    private RateLimitService rateLimitService;

    public NewInTaskObserver(){
        this.lastPublishTime = System.currentTimeMillis();
    }
    @Override
    public String getName() {
        return "NewInTaskObserver";
    }

    @Override
    public String getInstance() {
        return EnvironmentUtils.getServerAddress();
    }

    @Override
    protected List<LaunchableExchangisTask> onPublishNext(String instance, int batchSize){
        List<LaunchableExchangisTask> tasks = observerService.onPublishLaunchAbleTask(instance, batchSize);
        if (!tasks.isEmpty()) {
            LOG.info("Publish the launch-able tasks waiting to be launched from database, size: [{}], cache_queue: [{}], last_task_id: [{}]",
                    tasks.size(), getCacheQueue().size(), tasks.get(0).getId());
        }
        return tasks;
    }


    @Override
    public int subscribe(List<LaunchableExchangisTask> publishedTasks) throws ExchangisTaskObserverException {
        Iterator<LaunchableExchangisTask> iterator = publishedTasks.iterator();
        TaskExecution<?> taskExecution = getTaskExecution();
        if (Objects.isNull(taskExecution)){
            throw new ExchangisTaskObserverException("TaskExecution cannot be null, please set it before subscribing!", null);
        }
        while(iterator.hasNext()){
            LaunchableExchangisTask launchableExchangisTask = iterator.next();
            if (Objects.nonNull(launchableExchangisTask)){
                try {
                    AtomicBoolean noParallel = new AtomicBoolean(false);
                    // Check the submittable condition first in order to avoid the duplicate scheduler tasks
                    SubmitSchedulerTask submitSchedulerTask = new SubmitSchedulerTask(rateLimitService, launchableExchangisTask,
                            () -> {
                                TaskParallelRule parallelRule = parallelManager.getOrCreateRule(launchableExchangisTask.getExecuteUser(),
                                        TaskParallelManager.Operation.SUBMIT);
                                if (parallelRule.incParallel()) {
                                    // check the status of launchedTask
                                    // insert or update launched task, status as TaskStatus.Scheduler
                                    boolean success =  observerService.subscribe(launchableExchangisTask);
                                    if (!success){
                                        parallelRule.decParallel(1);
                                    }
                                    return success;
                                }
                                noParallel.set(true);
                                return false;
                    }, (submitTask, e) -> {
                        TaskParallelRule parallelRule = parallelManager.getOrCreateRule(launchableExchangisTask.getExecuteUser(),
                                TaskParallelManager.Operation.SUBMIT);
                        // Decrease the parallel
                        parallelRule.decParallel(1);
                        if (Objects.nonNull(e)){
                            // Fail to submit, unsubscribe and discard it
                            observerService.unsubscribe(launchableExchangisTask);
                            discard(Collections.singletonList(launchableExchangisTask));
                        }
                    }, true);
                    if (submitSchedulerTask.isSubmitAble()) {
                        submitSchedulerTask.setTenancy(launchableExchangisTask.getExecuteUser());
                        try {
                            taskExecution.submit(submitSchedulerTask);
                        } catch (Exception e) {
                            // If the consumer queue is full?
                            LOG.warn("Internal_Error: Fail to async submit launch-able task: [ id: {}, name: {}, job_execution_id: {} ]"
                                    , launchableExchangisTask.getId(), launchableExchangisTask.getName(), launchableExchangisTask.getJobExecutionId(), e);
                            // Unsubscribe and discard the task
                            observerService.unsubscribe(launchableExchangisTask);
                            discard(Collections.singletonList(launchableExchangisTask));
                        }
                    } else if (noParallel.get()){
                        // Has no parallel
                        discard(Collections.singletonList(launchableExchangisTask));
                    }
                } catch (Exception e){
                    LOG.error("Internal_Error: Exception in subscribing task: [ id: {}, name: {}, job_execution_id: {} ]" +
                            ", please check your status of database and network",
                            launchableExchangisTask.getId(), launchableExchangisTask.getName(), launchableExchangisTask.getJobExecutionId(), e);
                    // Wait the other recover observers to subscribe the task
                }
            }
            iterator.remove();
        }
        return 0;
    }

    @Override
    public void discard(List<LaunchableExchangisTask> unsubscribeTasks) {
        // Calculate the delay time and requeue to the cache
        if (!unsubscribeTasks.isEmpty()) {
            try {
                observerService.delayToSubscribe(unsubscribeTasks);
            } catch (Exception e) {
                LOG.warn("Internal_Error: Exception in delaying unsubscribe tasks, reason:[{}]",
                        e.getMessage(), e);
                // Enforce to sleep
                try {
                    Thread.sleep(publishInterval);
                } catch (InterruptedException ex) {
                    //Ignore
                }
            }
            Queue<LaunchableExchangisTask> queue = getCacheQueue();
            unsubscribeTasks.forEach(queue::offer);
        }
    }
}