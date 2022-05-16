package com.webank.wedatasphere.exchangis.job.server.execution.generator.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

import java.util.List;

/**
 * Success event
 */
public class TaskGenerateSuccessEvent extends TaskGenerateEvent{

    /**
     * Generate result
     */
    private List<LaunchableExchangisTask> taskGenerated;

    public TaskGenerateSuccessEvent(LaunchableExchangisJob launchableExchangisJob) {
        super(System.currentTimeMillis(), launchableExchangisJob);
        taskGenerated = launchableExchangisJob.getLaunchableExchangisTasks();
    }

    public List<LaunchableExchangisTask> getTaskGenerated() {
        return taskGenerated;
    }

    public void setTaskGenerated(List<LaunchableExchangisTask> taskGenerated) {
        this.taskGenerated = taskGenerated;
    }
}
