package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import org.apache.ibatis.annotations.Param;

/**
 * @author tikazhang
 */
public interface ExchangisJobEntityDao {

    /**
     * Add new JobEntity
     * @param exchangisJobEntity
     */
    void addJobEntity(ExchangisJobEntity exchangisJobEntity);

    /**
     * Delete launchableJob
     * @param jobId
     */
    void deleteJobEntity(@Param("jobId") String jobId);

    /**
     * upgradeLaunchableJob
     * @param exchangisJobEntity
     */
    void upgradeJobEntity(ExchangisJobEntity exchangisJobEntity);

    /**
     * Get launchableTask
     * @param jobId
     */
    ExchangisJobEntity getJobEntity(@Param("jobId") String jobId);

}
