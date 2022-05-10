package com.webank.wedatasphere.exchangis.dss.appconn.service;


import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefImportService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisImportOperation;

/**
 * Ref import service
 */
public class ExchangisRefImportService extends AbstractRefImportService {

    @Override
    protected ExchangisImportOperation createRefImportOperation() {
        return new ExchangisImportOperation();
    }

}
