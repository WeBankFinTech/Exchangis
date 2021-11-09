package com.webank.wedatasphere.exchangis.appconn.project;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.ExchangisAppConn;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisProjectCreationOperation implements ProjectCreationOperation {
    private static Logger logger = LoggerFactory.getLogger(ExchangisProjectCreationOperation.class);


    private String getAppName() {
        return ExchangisConfig.EXCHANGIS_APPCONN_NAME;
    }
    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        String url="";
        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRequestRef.getWorkspace().getWorkspaceName());
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
