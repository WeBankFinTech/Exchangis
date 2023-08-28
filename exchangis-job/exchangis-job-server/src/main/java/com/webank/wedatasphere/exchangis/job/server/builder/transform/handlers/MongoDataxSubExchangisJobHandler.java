package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Params mapping for mongo in datax
 */
public class MongoDataxSubExchangisJobHandler extends AuthEnabledSubExchangisJobHandler {

    /**
     * Database
     */
    private static final JobParamDefine<String> DATABASE = JobParams.define("dbName", JobParamConstraints.DATABASE);

    /**
     * Host
     */
    private static final JobParamDefine<String> SOURCE_HOST = JobParams.define("conn_ins[0].host", JobParamConstraints.HOST);
    private static final JobParamDefine<String> SINK_HOST = JobParams.define("conn_ins[0].host", JobParamConstraints.HOST);

    /**
     * Port
     */
    private static final JobParamDefine<String> SOURCE_PORT = JobParams.define("conn_ins[0].port", JobParamConstraints.PORT);
    private static final JobParamDefine<String> SINK_PORT = JobParams.define("conn_ins[0].port", JobParamConstraints.PORT);

    /**
     * Connect params
     */
    private static final JobParamDefine<Map<String, Object>> OPTION_PARAMS = JobParams.define("optionParams", JobParamConstraints.CONNECT_PARAMS,
            connectParams -> Json.fromJson(connectParams, Map.class), String.class);
    /**
     * Collection name(table)
     */
    private static final JobParamDefine<String> COLLECTION_NAME = JobParams.define("collectionName", JobParamConstraints.TABLE);

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
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
        if (Objects.nonNull(paramSet)){
            Arrays.asList(sourceMappings()).forEach(define -> paramSet.addNonNull(define.get(paramSet)));
        }
    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(paramSet)){
            Arrays.asList(sinkMappings()).forEach(define -> paramSet.addNonNull(define.get(paramSet)));
        }
    }

    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[]{SOURCE_HOST, SOURCE_PORT, USERNAME, PASSWORD, DATABASE, COLLECTION_NAME, OPTION_PARAMS};
    }

    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[]{SINK_HOST, SINK_PORT, USERNAME, PASSWORD, DATABASE, COLLECTION_NAME, OPTION_PARAMS};
    }
}
