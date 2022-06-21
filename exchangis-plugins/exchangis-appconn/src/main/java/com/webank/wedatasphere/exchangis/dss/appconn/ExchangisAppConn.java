package com.webank.wedatasphere.exchangis.dss.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import org.apache.linkis.common.conf.CommonVars;

/**
 * Exchangis AppConn top implement
 */
public class ExchangisAppConn extends AbstractOnlySSOAppConn implements ThirdlyAppConn {

    /**
     * Project service operation
     */
    private ExchangisStructureIntegrationStandard exchangisStructureIntegrationStandard;

    /**
     * Operation for flow node
     */
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
        exchangisStructureIntegrationStandard = ExchangisStructureIntegrationStandard.getInstance();
        exchangisDevelopmentIntegrationStandard = ExchangisDevelopmentIntegrationStandard.getInstance();
    }
}
