package com.webank.wedatasphere.exchangis.dss.appconn;

import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;
import com.webank.wedatasphere.exchangis.dss.appconn.service.*;

/**
 * Develop integration
 */
public class ExchangisDevelopmentIntegrationStandard  extends AbstractDevelopmentIntegrationStandard {

    @Override
    protected RefCRUDService createRefCRUDService() {
        return new ExchangisRefCRUDService();
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new ExchangisRefExecutionService();
    }

    @Override
    protected RefExportService createRefExportService() {
        return new ExchangisRefExportService();
    }

    @Override
    protected RefImportService createRefImportService() {
        return new ExchangisRefImportService();
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return new ExchangisRefQueryService();
    }
}
