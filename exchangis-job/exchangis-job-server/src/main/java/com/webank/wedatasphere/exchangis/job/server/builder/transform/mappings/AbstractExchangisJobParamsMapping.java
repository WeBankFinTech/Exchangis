package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.AuthEnabledSubExchangisJobHandler;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implement "SubExchangisJobHandler", only handle the params of job
 */
public abstract class AbstractExchangisJobParamsMapping extends AuthEnabledSubExchangisJobHandler {

    @Override
    public void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        subExchangisJob.getSourceColumns().forEach(srcColumnMappingFunc());
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
        if (Objects.nonNull(paramSet)){
//            info("SourceParamSet: {}", Json.toJson(paramSet.toList().stream().collect(
//                    Collectors.toMap(JobParam::getStrKey, JobParam::getValue)), null));
            Optional.ofNullable(sourceMappings()).ifPresent(jobParamDefines ->
                    Arrays.asList(jobParamDefines).forEach(define -> paramSet.addNonNull(define.get(paramSet))));
        }
    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        subExchangisJob.getSinkColumns().forEach(sinkColumnMappingFunc());
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(paramSet)){
//            info("SinkParamSet: {}", Json.toJson(paramSet.toList().stream().collect(
//                    Collectors.toMap(JobParam::getStrKey, JobParam::getValue)), null));
            Optional.ofNullable(sinkMappings()).ifPresent(jobParamDefines ->
                    Arrays.asList(jobParamDefines).forEach(define -> paramSet.addNonNull(define.get(paramSet))));
        }
    }

    /**
     * Get param definition of source mapping
     * @return definitions
     */
    public abstract JobParamDefine<?>[] sourceMappings();


    /**
     * Get param definition of sink mapping
     * @return
     */
    public abstract JobParamDefine<?>[] sinkMappings();

    /**
     * Source columns mapping function
     * @return consumer function
     */
    protected Consumer<SubExchangisJob.ColumnDefine> srcColumnMappingFunc(){
        return columnDefine -> {};
    }

    /**
     * Sink columns mapping function
     * @return consumer function
     */
    protected Consumer<SubExchangisJob.ColumnDefine> sinkColumnMappingFunc(){
        return columnDefine -> {};
    }
}
