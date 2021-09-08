package com.webank.wedatasphere.exchangis.job.server.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;

/**
 * The interface Exchangis job service.
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
public interface ExchangisJobService extends IService<ExchangisJob> {

    /**
     * Create job.
     *
     * @param exchangisJobBasicInfoDTO
     *            the exchangis job basic info dto
     * @return the exchangis job basic info vo
     */
    public ExchangisJobBasicInfoVO createJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO);

    /**
     * Gets job list(return job basic info).
     *
     * @param projectId
     *            the project id
     * @param type
     *            the type
     * @param name
     *            the name
     * @return the job list
     */
    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String type, String name);

    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId);

    /**
     * Update job exchangis job basic info.
     *
     * @param exchangisJobBasicInfoDTO
     *            the exchangis job basic info dto
     * @param id
     *            the id
     * @return the exchangis job basic info vo
     */
    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id);

    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile);

    public void deleteJob(Long id);

    /**
     * Get exchangis job by id.
     *
     * @param id
     *            the id
     * @return the job
     * @throws ExchangisJobErrorException
     *             the exchangis job error exception
     */
    public ExchangisJob getJob(Long id) throws ExchangisJobErrorException;

    /**
     * Update exchangis job config and subjobs content.
     *
     * @param exchangisJobContentDTO
     *            the exchangis job content dto
     * @param id
     *            the id
     * @return the exchangis job
     */
    public ExchangisJob updateJob(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
        throws ExchangisJobErrorException;
}
