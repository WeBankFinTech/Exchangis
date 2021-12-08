package com.webank.wedatasphere.exchangis.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef;

public class ExchangisCompletedExecutionResponseRef  extends CompletedExecutionResponseRef {
    public ExchangisCompletedExecutionResponseRef(int status) {
        super(status);
    }

    public ExchangisCompletedExecutionResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }
}
