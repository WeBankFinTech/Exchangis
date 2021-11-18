package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;

public class SqoopExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    private static final Logger LOG = LoggerFactory.getLogger(SqoopExchangisEngineJobBuilder.class);

    /**
     * Mapping params
     */
    private static final JobParamDefine<List<Map<String, Object>>> TRANSFORM_MAPPING = JobParams.define("mapping");
    private static final JobParamDefine<String> SOURCE_FIELD_NAME = JobParams.define("name", "source_field_name", String.class);
    private static final JobParamDefine<String> SINK_FIELD_NAME = JobParams.define("name", "sink_field_name", String.class);
    private static final JobParamDefine<String> QUERY_STRING = JobParams.define("sqoop.args.query");

    private static final JobParamDefine<String> COLUMN_SERIAL = JobParams.define("sqoop.args.columns", (BiFunction<String, DataSourceJobParamSet,String>) (key, paramSet)->{
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet.jobParamSet).getValue();
        List<String> columnSerial = new ArrayList<>();
        if(Objects.nonNull(mappings)){
            if("mysql".equalsIgnoreCase(paramSet.sourceType)) {
                mappings.forEach(mapping -> Optional.ofNullable(SOURCE_FIELD_NAME.newParam(mapping).getValue()).ifPresent(columnSerial::add));
            }else if("mysql".equalsIgnoreCase(paramSet.sinkType)){
                mappings.forEach(mapping -> Optional.ofNullable(SINK_FIELD_NAME.newParam(mapping).getValue()).ifPresent(columnSerial::add));
            }
        }
        return StringUtils.join(columnSerial, ",");
    });
    @Override
    public int priority() {
        return 1;
    }

    @Override
    public ExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        try {
            SqoopExchangisEngineJob engineJob = new SqoopExchangisEngineJob();
            //pass-through the params
            Map<String, Object> sqoopParams = new HashMap<>();
            engineJob.getJobContent().put("sqoop-params", sqoopParams);
            sqoopParams.putAll(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false));
            sqoopParams.putAll(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_CONTENT_SINK, false));
            sqoopParams.putAll(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_CONTENT_SOURCE, false));
            resolveTransformMappings(inputJob, inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING), sqoopParams);
            if (Objects.nonNull(expectJob)) {
                engineJob.setJobName(expectJob.getJobName());
                engineJob.setRuntimeParams(expectJob.getRuntimeParams());
                engineJob.setEngine(expectJob.getEngine());
            }
            return engineJob;
        }catch(Exception e){
            throw new ExchangisJobException(ExchangisJobExceptionCode.ENGINE_JOB_ERROR.getCode(),
                    "Fail to build sqoop engine job, message:[" + e.getMessage() + "]", e);
        }
    }

    @Override
    public boolean canBuild(SubExchangisJob inputJob) {
        return "sqoop".equalsIgnoreCase(inputJob.getEngine());
    }

    /**
     * Resolve transform jobParams
     * @param transformJobParamSet param set
     */
    private void resolveTransformMappings(SubExchangisJob subExchangisJob, JobParamSet transformJobParamSet, Map<String, Object> sqoopParams){
        String queryString = QUERY_STRING.newParam(sqoopParams).getValue();
        if(StringUtils.isBlank(queryString)){
            DataSourceJobParamSet dataSourceJobParamSet = new DataSourceJobParamSet();
            dataSourceJobParamSet.sourceType = subExchangisJob.getSourceType();
            dataSourceJobParamSet.sinkType = subExchangisJob.getSinkType();
            dataSourceJobParamSet.jobParamSet = transformJobParamSet;
            String column = COLUMN_SERIAL.newParam(dataSourceJobParamSet).getValue();
            sqoopParams.put(COLUMN_SERIAL.getKey(), column);
        }
    }

    private static class DataSourceJobParamSet{
        /**
         * type
         */
        private String sinkType;

        private String sourceType;
        /**
         * Job param set
         */
        private JobParamSet jobParamSet;
    }
}
