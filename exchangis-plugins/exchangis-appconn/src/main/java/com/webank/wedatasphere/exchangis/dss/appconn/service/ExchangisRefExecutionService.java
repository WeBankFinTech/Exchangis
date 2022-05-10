package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExecutionService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefExecutionOperation;

public class ExchangisRefExecutionService extends AbstractRefExecutionService {

    @Override
    protected RefExecutionOperation createRefExecutionOperation() {
        return new ExchangisRefExecutionOperation();
    }

}
