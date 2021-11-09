package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;

/**
 * Mysql Handler
 */
public class MySQLExchangisJobHandler extends AbstractExchangisJobParamsHandler{
    @Override
    public void handleSource(JobParamSet sourceParams, ExchangisJob originJob) {

    }

    @Override
    public void handleSink(JobParamSet sinkParams, ExchangisJob originJob) {

    }

    @Override
    public String dataSourceType() {
        return "mysql";
    }
}
