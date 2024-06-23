package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 * @Date 2022/1/17 21:58
 */
public interface LaunchableTaskDao {

    /**
     * Add new launchableTask
     * @param tasks
     */
    void addLaunchableTask(@Param("tasks")List<LaunchableExchangisTask> tasks);

    /**
     * Delete launchableTask
     * @param taskId
     */
    void deleteLaunchableTask(@Param("taskId") String taskId);

    /**
     * Delay launchableTask to designated time
     * @param taskId task id
     * @param delayTime delay time
     */
    void delayLaunchableTask(@Param("taskId") String taskId, Date delayTime);

    /**
     * upgradeLaunchableTask
     * @param launchableExchangisTask
     */
    void upgradeLaunchableTask(LaunchableExchangisTask launchableExchangisTask);

    /**
     * Get launchableTask
     * @param taskId
     */
    LaunchableExchangisTask getLaunchableTask(@Param("taskId") String taskId);

    /**
     * Get Tasks need to execute
     * @param instance ins
     */

    List<LaunchableExchangisTask> getTaskToLaunch(@Param("instance")String instance,
                                                  @Param("limitSize") Integer limitSize);

    /**
     * Get the expired tasks need to execute
     * @param status expire status
     * @param instance ins
     * @param expireTime expire time
     * @param limitSize limit size
     * @return
     */
    List<LaunchableExchangisTask> getTaskToLaunchInExpire(@Param("instance")String instance,
                                                          @Param("status")String status,
                                                          @Param("expireTime") Date expireTime,
                                                          @Param("limitSize")Integer limitSize);

    /**
     * Batch delay
     * @param tasks tasks
     */
    void delayBatch(@Param("tasks")List<LaunchableExchangisTask> tasks);
}
