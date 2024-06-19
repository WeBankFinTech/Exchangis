package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelManager;
import com.webank.wedatasphere.exchangis.job.server.execution.parallel.TaskParallelRule;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.SubmitSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Subscribe the new task from database and then submit to scheduler
 */
@Component
public class NewInTaskObserver extends CacheInTaskObserver<LaunchableExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(NewInTaskObserver.class);

    @Resource
    private TaskObserverService taskObserverService;

    @Resource
    private TaskParallelManager parallelManager;

    @Override
    protected List<LaunchableExchangisTask> onPublishNext(int batchSize){
        // Get the launchable task from launchable task inner join launched task
        List<LaunchableExchangisTask> tasks = taskObserverService.onPublishLaunchableTask(batchSize);
        if (!tasks.isEmpty()) {
            LOG.debug("Get the launchable task from database, size: [{}]", tasks.size());
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
                    // Check the submittable condition first in order to avoid the duplicate scheduler tasks
                    SubmitSchedulerTask submitSchedulerTask = new SubmitSchedulerTask(launchableExchangisTask,
                            () -> {
                                TaskParallelRule parallelRule = parallelManager.getOrCreateRule(launchableExchangisTask.getExecuteUser(),
                                        TaskParallelManager.Operation.SUBMIT);
                                if (parallelRule.incParallel()) {
                                    // check the status of launchedTask
                                    // insert or update launched task, status as TaskStatus.Scheduler
                                    boolean success =  taskObserverService.subscribe(launchableExchangisTask);
                                    if (!success){
                                        parallelRule.decParallel(1);
                                    }
                                    return success;
                                }
                                return false;
                    }, (submitTask, e) -> {
                        TaskParallelRule parallelRule = parallelManager.getOrCreateRule(launchableExchangisTask.getExecuteUser(),
                                TaskParallelManager.Operation.SUBMIT);
                        // Decrease the parallel
                        parallelRule.decParallel(1);
                    }, true);
                    if (submitSchedulerTask.isSubmitAble()) {
                        submitSchedulerTask.setTenancy(launchableExchangisTask.getExecuteUser());
                        try {
                            taskExecution.submit(submitSchedulerTask);
                        } catch (Exception e) {
                            // If the consumer queue is full?
                            LOG.warn("Internal_Error: Fail to async submit launchable task: [ id: {}, name: {}, job_execution_id: {} ]"
                                    , launchableExchangisTask.getId(), launchableExchangisTask.getName(), launchableExchangisTask.getJobExecutionId(), e);
                            // Unsubscribe the task
                            taskObserverService.unsubscribe(launchableExchangisTask);
                        }
                    }
                } catch (Exception e){
                    LOG.error("Exception in subscribing launchable tasks, please check your status of database and network", e);
                }
            }
            iterator.remove();
        }
        return 0;
    }

    @Override
    public String getName() {
        return "NewInTaskObserver";
    }
}
