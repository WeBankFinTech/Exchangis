package com.webank.wedatasphere.exchangis.job.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
public interface ExchangisJobService extends IService<ExchangisJob> {

    public ExchangisJobBasicInfoVO createJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO);

    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String type, String name);

    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId);

    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id);

    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile);

    public void deleteJob(Long id);

    public ExchangisJobBasicInfoVO getJob(Long id);
}
