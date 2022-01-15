package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tikazhang
 */
public interface ExchangisExecutionTaskDao {
    /**
     * Get Tasks need to execute
     * @param
     */

    List<LaunchableExchangisTask> getTaskTolaunch();

    /**
     * jubge task whether launch or not
     * param launchedExchangisTaskEntity
     */
    void LaunchedTaskOrNot(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * insert launchedTask
     * @param launchedExchangisTaskEntity
     */
    void insertLaunchedTask(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * delete launchedTask
     * @param taskId
     */
    void deleteLaunchedTask(@Param("taskId")String taskId);

    /**
     * upgrade launchedTask
     * @param launchedExchangisTaskEntity
     */
    void upgradeLaunchedTask(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * search launchedTask
     * @param taskId
     */

    LaunchedExchangisTaskEntity getLaunchedTaskEntity(@Param("taskId") String taskId);

    /**
     * upgrade launchedTask metrics
     * @param metrics
     * @param taskId
     */

    void upgradeLaunchedTaskMtrics(@Param("metrics") String metrics, @Param("taskId") String taskId);

    /**
     * upgrade launchedTask status
     * @param status
     * @param taskId
     */

    void upgradeLaunchedTaskStatus(@Param("status") String status, @Param("taskId") String taskId);
}
