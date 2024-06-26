package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.execution.AbstractTaskExecution;

import java.util.Date;
import java.util.List;

/**
 * Task observer service
 */
public interface TaskObserverService {

    /**
     * Get the launch-able task
     * @param instance instance
     * @param limitSize limit size
     * @return list
     */
    List<LaunchableExchangisTask> onPublishLaunchAbleTask(String instance, int limitSize);

    /**
     * Get the launch-able task which is expired
     * @param instance instance
     * @param expireTime expire time
     * @param limitSize limit size
     * @return list
     */
    List<LaunchableExchangisTask> onPublishLaunchAbleTaskInExpire(String instance,
                                                                  Date expireTime, int limitSize);

    /**
     * Get the launched task which is expired
     * @param instance instance
     * @param expireTime expire time
     * @param limitSize limit size
     * @return list
     */
    List<LaunchedExchangisTaskEntity> onPublishLaunchedTaskInExpire(String instance,
                                                              Date expireTime, int limitSize);

    /**
     * Get the launched job which is expired
     * @param instance instance
     * @param expireTime expire time
     * @param limitSize limit size
     * @return
     */
    List<LaunchedExchangisJobEntity> onPublishLaunchedJobInExpire(String instance,
                                                                  Date expireTime, int limitSize);
    /**
     * Subscribe the launch-able task
     * @param task task
     * @return boolean
     */
    boolean subscribe(LaunchableExchangisTask task);

    /**
     * Subscribe the launched task
     * @param task task
     * @return bolean
     */
    boolean subscribe(LaunchedExchangisTaskEntity task);

    /**
     * Subscribe the launched job
     * @param job job
     * @return boolean
     */
    boolean subscribe(LaunchedExchangisJobEntity job);
    /**
     * Unsubscribe the launch-able task
     * @param task task
     */
    void unsubscribe(LaunchableExchangisTask task);

    /**
     * Delay the schedule time of launch-able task
     * @param tasks task
     */
    void delayToSubscribe(List<LaunchableExchangisTask> tasks);

    void setTaskExecution(AbstractTaskExecution taskExecution);
}
