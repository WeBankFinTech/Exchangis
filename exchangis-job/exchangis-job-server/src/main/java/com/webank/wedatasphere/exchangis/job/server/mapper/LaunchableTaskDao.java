package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.apache.ibatis.annotations.Param;

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
     * @param
     */

    List<LaunchableExchangisTask> getTaskToLaunch(@Param("limitSize") Integer limitSize);
}
