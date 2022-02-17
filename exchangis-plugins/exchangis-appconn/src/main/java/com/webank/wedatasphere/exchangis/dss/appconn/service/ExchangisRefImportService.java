package com.webank.wedatasphere.exchangis.dss.appconn.service;


import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefImportService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisImportOperation;

public class ExchangisRefImportService extends AbstractRefImportService {

    @Override
    protected RefImportOperation createRefImportOperation() {
        return new ExchangisImportOperation(this);
    }
}
