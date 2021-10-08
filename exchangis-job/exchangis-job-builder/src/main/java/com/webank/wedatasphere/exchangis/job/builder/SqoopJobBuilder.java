package com.webank.wedatasphere.exchangis.job.builder;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;

public class SqoopJobBuilder implements ExchangisJobBuilder {
    @Override
    public ExchangisLaunchTask[] buildJob(ExchangisJob job) {
        return new ExchangisLaunchTask[0];
    }
}
