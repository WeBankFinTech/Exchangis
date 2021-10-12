package com.webank.wedatasphere.exchangis.job.server.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;

@Service
public class ExchangisLaunchTaskServiceImpl extends ServiceImpl<ExchangisLaunchTaskMapper, ExchangisLaunchTask>
    implements ExchangisLaunchTaskService {}
