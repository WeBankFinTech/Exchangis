package com.webank.wedatasphere.exchangis.job.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;

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
}
