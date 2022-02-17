package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefQueryService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisQueryOperation;

public class ExchangisQueryService extends AbstractRefQueryService {
    @Override
    protected RefQueryOperation createRefQueryOperation() {
        return new ExchangisQueryOperation(this);
    }
}
