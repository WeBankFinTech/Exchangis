package com.webank.wedatasphere.exchangis.job.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;

import java.util.List;

public interface ExchangisJobDsBindService {

    public void updateJobDsBind(Long jobId, List<ExchangisJobDsBind> dsBinds);

    public boolean inUse(Long datasourceId);

}
