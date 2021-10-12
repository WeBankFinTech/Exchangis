package com.webank.wedatasphere.exchangis.job.builder;

import java.util.Collections;
import java.util.List;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;

public class SqoopJobBuilder implements ExchangisJobBuilder {
    @Override
    public List<ExchangisLaunchTask> buildJob(ExchangisJob job) {
        return Collections.emptyList();
    }
}
