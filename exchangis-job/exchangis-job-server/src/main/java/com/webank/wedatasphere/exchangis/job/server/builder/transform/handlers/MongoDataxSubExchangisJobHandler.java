package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Map;

/**
 * Params mapping for mongo in datax
 */
public class MongoDataxSubExchangisJobHandler extends AbstractLoggingSubExchangisJobHandler {

    /**
     * Database
     */
    private static final JobParamDefine<String> DATABASE = JobParams.define("dbName", JobParamConstraints.DATABASE);

    /**
     * Connect params
     */
    private static final JobParamDefine<String> OPTION_PARAMS = JobParams.define("optionParams", JobParamConstraints.CONNECT_PARAMS,
            connectParams -> Json.fromJson(connectParams, Map.class), String.class);

//    private static final JobParamDefine<String> QUERY = JobParams.define("query", JobParams.define())


//    private static final JobParamDefine<String>
    @Override
    public String dataSourceType() {
        return "mongodb";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "datax".equalsIgnoreCase(engineType);
    }


    @Override
    public void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {

    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {

    }

    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[0];
    }

    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[0];
    }
}
