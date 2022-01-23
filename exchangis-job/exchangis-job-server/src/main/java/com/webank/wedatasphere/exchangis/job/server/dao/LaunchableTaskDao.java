package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/1/17 21:58
 */
public interface LaunchableTaskDao {

    /**
     * Add new launchableTask
     * @param launchableExchangisTask
     */
    void addLaunchableTask(LaunchableExchangisTask launchableExchangisTask);

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
