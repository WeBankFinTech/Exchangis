package com.webank.wedatasphere.exchangis.job.builder;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;

import java.util.List;

public abstract class ExchangisJobBuilder {
    public abstract List<ExchangisLaunchTask> buildJob(ExchangisJob job) throws ExchangisDataSourceException;
}
