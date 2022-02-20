package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExportService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisExportOperation;

/**
 * Ref export service
 */
public class ExchangisRefExportService extends AbstractRefExportService {
    @Override
    @SuppressWarnings("unchecked")
    protected <K extends RequestRef> RefExportOperation<K> createRefExportOperation() {
        return (RefExportOperation<K>) new ExchangisExportOperation(this);
    }

}
