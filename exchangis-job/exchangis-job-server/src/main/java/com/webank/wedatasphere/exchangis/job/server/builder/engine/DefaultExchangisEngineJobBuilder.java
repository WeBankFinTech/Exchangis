package com.webank.wedatasphere.exchangis.job.server.builder.engine;


import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implements
 */
public class DefaultExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    private static final String ENGINE_JOB_MEMORY_USED = "setting.max.memory";
    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        String paramsString = ctx.getOriginalJob().getJobParams();
        ExchangisEngineJob exchangisEngineJob = new ExchangisEngineJob();
        if (StringUtils.isNotBlank(paramsString)){
            Map<String, Object> jobParams = Json.fromJson(paramsString, Map.class);
            if (Objects.nonNull(jobParams)){
                exchangisEngineJob.getJobContent().putAll(jobParams);
            }
        }
        Map<String, Object> settings = inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false);
        Optional.ofNullable(settings.get(ENGINE_JOB_MEMORY_USED)).ifPresent(memoryUsed -> exchangisEngineJob.setMemoryUsed(Long.valueOf(String.valueOf(memoryUsed))));
        exchangisEngineJob.setRuntimeParams(settings);
        exchangisEngineJob.setEngineType(ctx.getOriginalJob().getEngineType());
        exchangisEngineJob.setName(inputJob.getName());
        exchangisEngineJob.setJobLabel(ctx.getOriginalJob().getJobLabel());
        return exchangisEngineJob;
    }
}
