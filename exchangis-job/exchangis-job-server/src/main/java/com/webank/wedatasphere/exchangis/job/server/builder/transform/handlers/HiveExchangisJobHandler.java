package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;

/**
 * Job handler of Hive
 */
public class HiveExchangisJobHandler extends AbstractExchangisJobParamsHandler{
    @Override
    public void handleSource(JobParamSet sourceParams, ExchangisJob originJob) {
        //Unit test data
        sourceParams.add(JobParams.define("version", "source.version"));
        sourceParams.add(JobParams.newOne("version", "1.4.6"));
        sourceParams.add(JobParams.define("version", () -> "1.4.6"));
    }

    @Override
    public void handleSink(JobParamSet sinkParams, ExchangisJob originJob) {

    }

    @Override
    public String dataSourceType() {
        return "hive";
    }
}
