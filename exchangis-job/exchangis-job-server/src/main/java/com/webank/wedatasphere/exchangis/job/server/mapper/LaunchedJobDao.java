package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

import java.util.List;

/**
 *
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
     * Search log path and status info
     * @param jobExecutionId execution id
     * @return
     */
    LaunchedExchangisJobEntity searchLogPathInfo(@Param("jobExecutionId")String jobExecutionId);
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
     * To upgrade launchedJob progress
     * @param jobExecutionId execution id
     * @param totalTaskProgress progress of total related task
     * @param updateTime updateTime
     */
    int upgradeLaunchedJobProgress(@Param("jobExecutionId")String jobExecutionId, @Param("totalTaskProgress") Float totalTaskProgress,
                                         @Param("updateTime")Date updateTime);

    /**
     * get All launchJob
     * @return job entity list
     */
    List<LaunchedExchangisJobEntity> getAllLaunchedJob(@Param("jobExecutionId") String jobExecutionId, @Param("jobName") String jobName, @Param("status") String status, @Param("launchStartTime") Date launchStartTime, @Param("launchEndTime") Date launchEndTime, @Param("start")int start, @Param("size") int size, @Param("loginUser") String loginUser);

    /**
     * get launchJob count
     * return job entity number
     */
    int count(@Param("jobExecutionId") String jobExecutionId, @Param("jobName") String jobName, @Param("status") String status, @Param("launchStartTime") Date launchStartTime, @Param("launchEndTime") Date launchEndTime, @Param("loginUser") String loginUser);

    /**
     * delete launchJob
     */
    void deleteJob(@Param("jobExecutionId")String jobExecutionId);

}
