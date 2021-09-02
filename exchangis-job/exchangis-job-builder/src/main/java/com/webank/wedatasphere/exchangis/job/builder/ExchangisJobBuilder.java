package com.webank.wedatasphere.exchangis.job.builder;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.LaunchTask;

public interface ExchangisJobBuilder {
    LaunchTask[] buildJob(ExchangisJob job);
}
