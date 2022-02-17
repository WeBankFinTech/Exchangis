package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExportService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisExportOperation;

public class ExchangisRefExportService extends AbstractRefExportService {
    @Override
    protected RefExportOperation createRefExportOperation() {
        return new ExchangisExportOperation(this);
    }
}
