package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;

import java.util.Objects;

/**
 *  Datax engine job
 */
public class DataxExchangisEngineJob extends ExchangisEngineJob {

    private static final String CODE_NAME = "job";

    public DataxExchangisEngineJob(ExchangisEngineJob engineJob){
        super(engineJob);
    }
    @SuppressWarnings({"unchecked"})
    public <T>T getCode(Class<?> type) {
        Object code = super.getJobContent().get(CODE_NAME);
        if (Objects.nonNull(code) && type.isAssignableFrom(code.getClass())){
            return (T)code;
        }
        return null;
    }

    public void setCode(Object code) {
        super.getJobContent().put(CODE_NAME, code);
    }
}
