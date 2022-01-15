package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author tikazhang
 */
public interface ExchangisExecutionJobDao {
    /**
     * insert launchJob
     * @param launchedExchangisJobEntity
     */

    void insertLaunchedJob(LaunchedExchangisJobEntity launchedExchangisJobEntity);

    /**
     * upgrade launchJob status
     */

    void upgradeLaunchedJobStatus(@Param("jobExecutionId")String jobExecutionId, @Param("status") String stutus);
}
