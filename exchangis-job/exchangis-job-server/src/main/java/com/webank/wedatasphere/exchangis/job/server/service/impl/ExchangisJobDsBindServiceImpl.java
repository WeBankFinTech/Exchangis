package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobDsBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExchangisJobDsBindServiceImpl implements ExchangisJobDsBindService {

    @Autowired
    private ExchangisJobDsBindMapper dsBindMapper;

    @Override
    public void updateJobDsBind(Long jobId, List<ExchangisJobDsBind> dsBinds) {

        QueryWrapper<ExchangisJobDsBind> deleteCondition = new QueryWrapper<>();
        deleteCondition.eq("job_id", jobId);
        this.dsBindMapper.delete(deleteCondition);

        for (ExchangisJobDsBind dsBind : dsBinds) {
            this.dsBindMapper.insert(dsBind);
        }
    }

    @Override
    public boolean inUse(Long datasourceId) {
        QueryWrapper<ExchangisJobDsBind> condition = new QueryWrapper<>();
        condition.eq("source_ds_id", datasourceId).or().eq("sink_ds_id", datasourceId);
        Long count = Optional.ofNullable(this.dsBindMapper.selectCount(condition)).orElse(0L);
        return count > 0;
    }
}
