package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 */

public interface ExchangisJobEntityDao {

    /**
     * Add new JobEntity
     * @param exchangisJobEntity job entity
     */
    void addJobEntity(ExchangisJobEntity exchangisJobEntity);

    /**
     * Delete jobEntity
     * @param jobId job id
     */
    void deleteJobEntity(@Param("jobId") Long jobId);

    /**
     * upgrade jobEntity basic info
     * @param jobEntity job entity
     */
    void upgradeBasicInfo(ExchangisJobEntity jobEntity);

    /**
     * Upgrade config
     * @param jobEntity job entity
     */
    void upgradeConfig(ExchangisJobEntity jobEntity);

    /**
     * Upgrade content
     * @param jobEntity job entity
     */
    void upgradeContent(ExchangisJobEntity jobEntity);
    /**
     * Get jobEntity
     * @param jobId job id
     */
    ExchangisJobEntity getDetail(@Param("jobId") Long jobId);

    /**
     * Get jobEntity list
     * @param projectId
     * @return
     */
    List<ExchangisJobEntity> getDetailList(@Param("projectId") Long projectId);

    ExchangisJobEntity getBasicInfo(@Param("jobId") Long jobId);

    /**
     * Query page list
     * @param queryVo query vo
     * @return list
     */
    List<ExchangisJobEntity> queryPageList(ExchangisJobQueryVo queryVo);

    /**
     * Delete batch
     * @param ids id list
     */
    void deleteBatch(@Param("ids") List<Long> ids);

    List<ExchangisJobEntity> getByNameAndProjectId(@Param("jobName") String jobName, @Param("projectId") Long projectId);

    List<ExchangisJobEntity> getByNameWithProjectId(@Param("jobName") String jobName, @Param("projectId") Long projectId);
}
