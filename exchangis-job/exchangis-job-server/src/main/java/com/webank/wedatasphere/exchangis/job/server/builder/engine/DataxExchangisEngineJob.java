package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;

/**
 *  Datax engine job
 */
public class DataxExchangisEngineJob extends ExchangisEngineJob {

    private static final String CODE_NAME = "code";


    public String getCode() {
        return String.valueOf(super.getJobContent().get(CODE_NAME));
    }

    public void setCode(String code) {
        super.getJobContent().put(CODE_NAME, code);
    }
}
