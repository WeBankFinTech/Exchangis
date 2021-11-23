package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.exchangis.appconn.project.ExchangisProjectService;

public class ExchangisStructureIntegrationStandard extends AbstractStructureIntegrationStandard {
    @Override
    protected ProjectService createProjectService() {

        return new ExchangisProjectService();
    }
}
