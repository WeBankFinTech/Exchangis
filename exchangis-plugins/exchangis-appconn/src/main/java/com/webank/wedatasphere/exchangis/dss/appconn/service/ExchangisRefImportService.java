package com.webank.wedatasphere.exchangis.dss.appconn.service;


import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefImportService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisImportOperation;

/**
 * Ref import service
 */
public class ExchangisRefImportService extends AbstractRefImportService {
    @Override
    @SuppressWarnings("unchecked")
    protected <K extends RequestRef> RefImportOperation<K> createRefImportOperation() {
        return (RefImportOperation<K>) new ExchangisImportOperation(this);
    }

}
