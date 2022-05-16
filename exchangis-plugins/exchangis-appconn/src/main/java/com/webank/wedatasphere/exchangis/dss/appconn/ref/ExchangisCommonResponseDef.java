package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;

/**
 * Common response ref
 */
public class ExchangisCommonResponseDef extends AbstractExchangisResponseRef{

    protected ExchangisCommonResponseDef(String responseBody, int status) {
        super(responseBody, status);
    }

    public ExchangisCommonResponseDef(ExchangisEntityRespResult result) {
        super(result);
    }
}
