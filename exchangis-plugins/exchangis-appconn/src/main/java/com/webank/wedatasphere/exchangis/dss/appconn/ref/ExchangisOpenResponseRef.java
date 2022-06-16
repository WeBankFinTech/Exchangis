package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisNodeUtils;

/**
 * Ref open response
 */
public class ExchangisOpenResponseRef extends CommonResponseRef {
    public ExchangisOpenResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public String getSqoopId() throws ExternalOperationFailedException {
        return ExchangisNodeUtils.getSqoopId(responseBody);
    }

    public String getDataxId() throws ExternalOperationFailedException {
        return ExchangisNodeUtils.getDataxId(responseBody);
    }
}
