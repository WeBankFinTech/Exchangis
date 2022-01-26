package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author tikazhang
 */
public interface LaunchedJobDao {
    /**
     * insert launchedJob
     * @param launchedExchangisJobEntity entity
     */

    void insertLaunchedJob(LaunchedExchangisJobEntity launchedExchangisJobEntity);

    /**
     * delete launchedJob
     * @param jobExecutionId execution id
     */

    void deleteLaunchedJob(@Param("jobExecutionId")String jobExecutionId);

    /**
     * upgrade launchedJob
     * @param launchedExchangisJobEntity entity
     */
    void upgradeLaunchedJob(LaunchedExchangisJobEntity launchedExchangisJobEntity);

    /**
     * Update launch info
     * @param launchedExchangisJobEntity entity
     */
    void updateLaunchInfo(LaunchedExchangisJobEntity launchedExchangisJobEntity);
    /**
     * search launchJob
     * @param jobExecutionId execution id
     * @return entity
     */
    LaunchedExchangisJobEntity searchLaunchedJob(@Param("jobExecutionId")String jobExecutionId);

    /**
     * upgrade launchedJob status
     * @param jobExecutionId execution id
     * @param status status
     */

    void upgradeLaunchedJobStatus(@Param("jobExecutionId")String jobExecutionId, @Param("status") String status, @Param("updateTime")Date updateTime);

    /**
     * Try to upgrade launchedJob status in version control
     * @param jobExecutionId execution id
     * @param status update status
     * @param launchableTaskNum expected launchable task number
     * @param updateTime updateTime
     */
    int upgradeLaunchedJobStatusInVersion(@Param("jobExecutionId")String jobExecutionId, @Param("status") String status,
                                       @Param("launchableTaskNum") Integer launchableTaskNum,
                                       @Param("updateTime")Date updateTime);

    /**
     * Try to upgrade launchedJob progress in version control
     * @param jobExecutionId execution id
     * @param totalTaskProgress progress of total related task
     * @param updateTime updateTime
     */
    int upgradeLaunchedJobProgressInVersion(@Param("jobExecutionId")String jobExecutionId, @Param("totalTaskProgress") Float totalTaskProgress,
                                         @Param("updateTime")Date updateTime);
}
