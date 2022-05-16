package com.webank.wedatasphere.exchangis.dss.appconn;

import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.exchangis.dss.appconn.service.ExchangisProjectService;

/**
 * Structure(Project) service implement
 */
public class ExchangisStructureIntegrationStandard extends AbstractStructureIntegrationStandard {

    @Override
    protected ProjectService createProjectService() {
        return new ExchangisProjectService();
    }

}
