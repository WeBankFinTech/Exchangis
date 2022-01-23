package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

import java.util.List;

/**
 * Task observer service
 */
public interface TaskObserverService {

    /**
     * Get the launchable task
     * @param limitSize limit size
     * @return list
     */
    List<LaunchableExchangisTask> onPublishLaunchableTask(int limitSize);

    /**
     * Subscribe the launchable task
     * @param task task
     * @return boolean
     */
    boolean subscribe(LaunchableExchangisTask task);
}
