package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;

import java.util.Objects;

/**
 * Sqoop engine job
 */
public class SqoopExchangisEngineJob extends ExchangisEngineJob {
    //Empty

    public SqoopExchangisEngineJob(){

    }

    public SqoopExchangisEngineJob(ExchangisEngineJob engineJob){
        if (Objects.nonNull(engineJob)) {
            setName(engineJob.getName());
            setEngineType(engineJob.getEngineType());
            getJobContent().putAll(engineJob.getJobContent());
            getRuntimeParams().putAll(engineJob.getRuntimeParams());
            setMemoryUsed(engineJob.getMemoryUsed());
        }
    }
}
