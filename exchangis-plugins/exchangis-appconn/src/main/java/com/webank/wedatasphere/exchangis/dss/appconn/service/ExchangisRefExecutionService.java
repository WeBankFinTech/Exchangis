package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExecutionService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefExecutionOperation;

public class ExchangisRefExecutionService extends AbstractRefExecutionService {
    @Override
    public RefExecutionOperation createRefExecutionOperation() {
        ExchangisRefExecutionOperation exchangisExecutionOperation = new ExchangisRefExecutionOperation(this);
        return exchangisExecutionOperation;
    }
}
