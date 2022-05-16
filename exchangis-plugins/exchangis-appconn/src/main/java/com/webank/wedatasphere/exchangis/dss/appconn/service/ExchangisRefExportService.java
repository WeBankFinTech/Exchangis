package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExportService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisExportOperation;

/**
 * Ref export service
 */
public class ExchangisRefExportService extends AbstractRefExportService {

    @Override
    protected ExchangisExportOperation createRefExportOperation() {
        return new ExchangisExportOperation();
    }

}
