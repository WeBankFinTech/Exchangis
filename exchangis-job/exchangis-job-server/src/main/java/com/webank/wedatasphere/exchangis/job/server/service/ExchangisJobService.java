package com.webank.wedatasphere.exchangis.job.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskSpeedLimitVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The interface Exchangis job service.
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
public interface ExchangisJobService extends IService<ExchangisJobVO> {

    /**
     * Create job.
     *
     * @param exchangisJobBasicInfoDTO the exchangis job basic info dto
     * @return the exchangis job basic info vo
     */
    public ExchangisJobBasicInfoVO createJob(HttpServletRequest request, ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO);

    /**
     * Gets job list(return job basic info).
     *
     * @param projectId the project id
     * @param type      the type
     * @param name      the name
     * @return the job list
     */
    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String type, String name);

    public List<ExchangisJobBasicInfoVO> getJobListByDssProject(long dssProjectId, String type, String name);

    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId);

    /**
     * Update job exchangis job basic info.
     *
     * @param exchangisJobBasicInfoDTO the exchangis job basic info dto
     * @param id                       the id
     * @return the exchangis job basic info vo
     */
    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id);

    public ExchangisJobBasicInfoVO updateJobByDss(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, String nodeId);

    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile);

    public void deleteJob(Long id);

    public void deleteJobByDss(String nodeId);

    public ExchangisJobVO getJob(Long id) throws ExchangisJobServerException;
    /**
     * Get exchangis job by id.
     *
     * @param id the id
     * @return the job
     * @throws ExchangisJobServerException the exchangis job error exception
     */
    public ExchangisJobVO getJob(HttpServletRequest request, Long id) throws ExchangisJobServerException;

    public ExchangisJobVO getJobByDss(HttpServletRequest request, String nodeId) throws ExchangisJobServerException;

    /**
     * Update exchangis job config.
     *
     * @param exchangisJobContentDTO the exchangis job content dto
     * @param id                     the id
     * @return the exchangis job
     */
    public ExchangisJobVO updateJobConfig(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobServerException;

    /**
     * Update exchangis job content.
     *
     * @param exchangisJobContentDTO the exchangis job content dto
     * @param id                     the id
     * @return the exchangis job
     */
    public ExchangisJobVO updateJobContent(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobServerException, ExchangisDataSourceException;

     public  List<ElementUI<?>> getSpeedLimitSettings(Long id, String taskName);

     public void setSpeedLimitSettings(Long id, String taskName, ExchangisTaskSpeedLimitVO settings);
}
