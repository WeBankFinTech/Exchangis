package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;

public class ExchangisDevelopmentIntegrationStandard  extends AbstractDevelopmentIntegrationStandard {
    @Override
    protected RefCRUDService createRefCRUDService() {
        return null;
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return null;
    }

    @Override
    protected RefExportService createRefExportService() {
        return null;
    }

    @Override
    protected RefImportService createRefImportService() {
        return null;
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return null;
    }
}
