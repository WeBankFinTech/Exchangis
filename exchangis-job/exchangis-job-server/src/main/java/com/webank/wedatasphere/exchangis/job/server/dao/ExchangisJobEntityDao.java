package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    void deleteJobEntity(@Param("jobId") Long jobId);

    /**
     * upgradeLaunchableJob
     * @param exchangisJobEntity
     */
    void upgradeJobEntity(ExchangisJobEntity exchangisJobEntity);

    /**
     * Get launchableJob
     * @param jobId
     */
    ExchangisJobEntity getJobEntity(@Param("jobId") Long jobId);

    /**
     * Get launchableJobByFactor
     * @param projectId
     * @param name
     */
    List<ExchangisJobEntity> getJobEntityByFactor(@Param("projectId") long projectId, @Param("name") String name);

    /**
     * Get launchableJobByName
     * @param name
     */
    List<ExchangisJobEntity> getJobEntityByName(@Param("name") String name);

}
