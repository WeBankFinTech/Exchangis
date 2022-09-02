package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
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

    @Override
    protected List<LaunchableExchangisTask> onPublishNext(int batchSize){
        // Get the launchable task from launchable task inner join launched task
        List<LaunchableExchangisTask> tasks = taskObserverService.onPublishLaunchableTask(batchSize);
        if (!tasks.isEmpty()) {
            LOG.info("Get the launchable task from database, size: [{}]", tasks.size());
        }
        return tasks;
    }


    @Override
    public void subscribe(List<LaunchableExchangisTask> publishedTasks) throws ExchangisTaskObserverException {
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
                                // check the status of launchedTask
                                // insert or update launched task, status as TaskStatus.Scheduler
                                return taskObserverService.subscribe(launchableExchangisTask);
                    }, true);
                    if (submitSchedulerTask.isSubmitAble()) {
                        submitSchedulerTask.setTenancy(launchableExchangisTask.getExecuteUser());
                        try {
                            taskExecution.submit(submitSchedulerTask);
                        } catch (Exception e) {
                            LOG.warn("Fail to async submit launchable task: [ id: {}, name: {}, job_execution_id: {} ]"
                                    , launchableExchangisTask.getId(), launchableExchangisTask.getName(), launchableExchangisTask.getJobExecutionId(), e);
                        }
                    }
                } catch (Exception e){
                    LOG.error("Exception in subscribing launchable tasks, please check your status of database and network", e);
                }
            }
            iterator.remove();
        }
    }

    @Override
    public String getName() {
        return "NewInTaskObserver";
    }
}
