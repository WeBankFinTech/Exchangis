package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author tikazhang
 */
public interface LaunchedJobDao {
    /**
     * insert launchJob
     * @param launchedExchangisJobEntity entity
     */

    void insertLaunchedJob(LaunchedExchangisJobEntity launchedExchangisJobEntity);

    /**
     * delete launchJob
     * @param jobExecutionId execution id
     */

    void deleteLaunchedJob(@Param("jobExecutionId")String jobExecutionId);

    /**
     * upgrade launchJob
     * @param launchedExchangisJobEntity entity
     */
    void upgradeLaunchedJob(LaunchedExchangisJobEntity launchedExchangisJobEntity);

    /**
     * search launchJob
     * @param jobExecutionId execution id
     * @return entity
     */
    LaunchedExchangisJobEntity searchLaunchedJob(@Param("jobExecutionId")String jobExecutionId);

    /**
     * upgrade launchJob status
     * @param jobExecutionId execution id
     * @param status status
     */

    void upgradeLaunchedJobStatus(@Param("jobExecutionId")String jobExecutionId, @Param("status") String status, @Param("updateTime")Date updateTime);
}