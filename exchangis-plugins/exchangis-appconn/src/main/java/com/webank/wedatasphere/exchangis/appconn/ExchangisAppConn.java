package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class ExchangisAppConn extends AbstractOnlySSOAppConn implements ThirdlyAppConn {


    private ExchangisStructureIntegrationStandard exchangisStructureIntegrationStandard;
    private ExchangisDevelopmentIntegrationStandard exchangisDevelopmentIntegrationStandard;
    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return exchangisDevelopmentIntegrationStandard;
    }

    @Override
    public StructureIntegrationStandard getOrCreateStructureStandard() {
        return exchangisStructureIntegrationStandard;
    }

    @Override
    protected void initialize() {
        exchangisStructureIntegrationStandard = new ExchangisStructureIntegrationStandard();
        exchangisDevelopmentIntegrationStandard = new ExchangisDevelopmentIntegrationStandard();
    }
}
