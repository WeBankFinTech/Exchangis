package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.SubExchangisJobHandler;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Implement "SubExchangisJobHandler", only handle the params of job
 */
public abstract class AbstractExchangisJobParamsMapping implements SubExchangisJobHandler {

    @Override
    public void handleSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
        if (Objects.nonNull(paramSet)){
            Optional.ofNullable(sourceMappings()).ifPresent(jobParamDefines -> Arrays.asList(jobParamDefines).forEach(paramSet::addNonNull));
        }
    }

    @Override
    public void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(paramSet)){
            Optional.ofNullable(sourceMappings()).ifPresent(jobParamDefines -> Arrays.asList(jobParamDefines).forEach(paramSet::addNonNull));
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
}
