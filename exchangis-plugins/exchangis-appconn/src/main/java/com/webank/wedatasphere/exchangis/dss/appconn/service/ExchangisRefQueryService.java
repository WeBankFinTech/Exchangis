package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefQueryService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefQueryOperation;

/**
 * Ref query service
 */
public class ExchangisRefQueryService extends AbstractRefQueryService {
    @Override
    @SuppressWarnings("rawtypes")
    protected RefQueryOperation createRefQueryOperation() {
        return new ExchangisRefQueryOperation(this);
    }
}
