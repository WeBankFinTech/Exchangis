package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * The interface Exchangis job service.
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
public interface JobInfoService {

    /**
     * Create job.
     *
     * @param jobVo the exchangis job basic info
     * @return the exchangis job basic info vo
     */
    ExchangisJobVo createJob(ExchangisJobVo jobVo);

    /**
     * Update job exchangis job basic info.
     *
     * @param jobVo the exchangis job basic info dto
     * @return the exchangis job basic info vo
     */
    ExchangisJobVo updateJob(ExchangisJobVo jobVo);

    /**
     * Gets job list(return job basic info).
     *
     * @param queryVo query vo
     * @return the job page result
     */
    PageResult<ExchangisJobVo> queryJobList(ExchangisJobQueryVo queryVo);

    /**
     * Delete job
     * @param id job id
     */
    void deleteJob(Long id);

    /**
     * Get exchangis job by id.
     * TODO remove the request
     * @param id the id
     * @throws ExchangisJobServerException the exchangis job error exception
     */
    ExchangisJobVo getJob(Long id, boolean basic);

    /**
     * Get job by name and projectId
     * @param jobName
     * @param projectId
     * @return
     */
    List<ExchangisJobVo> getByNameAndProjectId(String jobName, Long projectId);

    /**
     * Get job by name and projectId
     * @param jobName
     * @param projectId
     * @return
     */
    List<ExchangisJobVo> getByNameWithProjectId(String jobName, Long projectId);

    ExchangisJobVo getDecoratedJob(HttpServletRequest request, Long id)  throws ExchangisJobServerException;

    /**
     * Get all subJob list
     * @param request
     * @param projectId
     * @return
     * @throws ExchangisJobServerException
     */
    List<ExchangisJobVo> getSubJobList(HttpServletRequest request, Long projectId)  throws ExchangisJobServerException;

    /**
     * Update exchangis job config.
     *
     * @param jobVo the exchangis job config
     * @return the exchangis job
     */
    ExchangisJobVo updateJobConfig(ExchangisJobVo jobVo) throws ExchangisJobServerException;

    /**
     * Update exchangis job content.
     *
     * @param jobVo the exchangis job content
     * @return the exchangis job
     */
    ExchangisJobVo updateJobContent(ExchangisJobVo jobVo) throws ExchangisJobServerException, ExchangisDataSourceException;

    /**
     * Copy job exchangis job basic info.
     *
     * @param jobVo the exchangis job basic info dto
     * @return the exchangis job basic info vo
     */
    ExchangisJobVo copyJob(ExchangisJobVo jobVo);

}
