package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefQueryService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefQueryOperation;

/**
 * Ref query service
 */
public class ExchangisRefQueryService extends AbstractRefQueryService {

    @Override
    protected ExchangisRefQueryOperation createRefQueryOperation() {
        return new ExchangisRefQueryOperation();
    }
}
