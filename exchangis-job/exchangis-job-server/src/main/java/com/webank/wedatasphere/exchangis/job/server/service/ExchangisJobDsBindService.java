package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;

import java.util.List;

public interface ExchangisJobDsBindService {

     void updateJobDsBind(Long jobId, List<ExchangisJobDsBind> dsBinds);

     boolean inUse(Long datasourceId);

}
