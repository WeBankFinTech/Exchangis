package com.webank.wedatasphere.exchangis.dss.appconn;

import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.exchangis.dss.appconn.service.ExchangisProjectService;

/**
 * Structure(Project) service implement
 */
public class ExchangisStructureIntegrationStandard extends AbstractStructureIntegrationStandard {

    private static ExchangisStructureIntegrationStandard instance;

    static{
        instance = new ExchangisStructureIntegrationStandard();
    }

    /**
     * Get the singleton instance
     * @return structure integration
     */
    public static ExchangisStructureIntegrationStandard getInstance(){
        return instance;
    }
    @Override
    protected ProjectService createProjectService() {
        return new ExchangisProjectService();
    }
}
