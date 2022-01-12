package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;

import java.util.List;

/**
 * Choose rule
 */
public interface TaskChooseRuler {

    /**
     * Choose the tasks from candidates with scheduler
     * @param candidates candidate task
     * @param taskScheduler task scheduler
     * @return
     */
    List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidates, TaskScheduler taskScheduler);
}
