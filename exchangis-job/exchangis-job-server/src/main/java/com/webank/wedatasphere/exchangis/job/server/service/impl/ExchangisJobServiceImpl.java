package com.webank.wedatasphere.exchangis.job.server.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@Service
public class ExchangisJobServiceImpl extends ServiceImpl<ExchangisJobMapper, ExchangisJob> implements ExchangisJobService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExchangisJobService exchangisJobService;

    @Override
    public ExchangisJobBasicInfoVO createJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJob exchangisJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJob.class);
        exchangisJobService.save(exchangisJob);
        return modelMapper.map(exchangisJob, ExchangisJobBasicInfoVO.class);
    }
}
