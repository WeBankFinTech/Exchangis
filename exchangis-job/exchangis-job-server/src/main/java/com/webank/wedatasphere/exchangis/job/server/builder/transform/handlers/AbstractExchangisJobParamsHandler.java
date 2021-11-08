package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.Objects;

/**
 * Implement "SubExchangisJobHandler", only handle the params of job
 */
public abstract class AbstractExchangisJobParamsHandler extends AbstractExchangisJobHandler implements SubExchangisJobHandler{

    @Override
    public void handleSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        super.handleSource(subExchangisJob, ctx);
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
        if (Objects.nonNull(paramSet)){
            handleSource(paramSet, ctx.getOriginalJob());
        }
    }

    @Override
    public void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        super.handleSink(subExchangisJob, ctx);
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(paramSet)){
            handleSink(paramSet, ctx.getOriginalJob());
        }
    }

    /**
     * Handle source params
     * @param sourceParams params
     * @param originJob origin job
     */
    public abstract void handleSource(JobParamSet sourceParams, ExchangisJob originJob);

    /**
     * Handle sink params
     * @param sinkParams params
     * @param originJob origin job
     */
    public abstract void handleSink(JobParamSet sinkParams, ExchangisJob originJob);
}
