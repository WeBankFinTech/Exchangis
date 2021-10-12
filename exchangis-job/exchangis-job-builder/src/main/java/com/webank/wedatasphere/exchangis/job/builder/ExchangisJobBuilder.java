package com.webank.wedatasphere.exchangis.job.builder;

import java.util.List;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;

public interface ExchangisJobBuilder {
    List<ExchangisLaunchTask> buildJob(ExchangisJob job) throws ExchangisDataSourceException;
}
