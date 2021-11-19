package com.webank.wedatasphere.exchangis.job.server.builder.engine;


import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Default implements
 */
public class DefaultExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        String paramsString = ctx.getOriginalJob().getJobParams();
        ExchangisEngineJob exchangisEngineJob = new ExchangisEngineJob();
        if (StringUtils.isNotBlank(paramsString)){
            Map<String, Object> runtimeParams = Json.fromJson(paramsString, Map.class);
            exchangisEngineJob.setRuntimeParams(runtimeParams);
        }
        exchangisEngineJob.setEngine(ctx.getOriginalJob().getEngineType());
        exchangisEngineJob.setJobName(inputJob.getJobName());
        return exchangisEngineJob;
    }
}
