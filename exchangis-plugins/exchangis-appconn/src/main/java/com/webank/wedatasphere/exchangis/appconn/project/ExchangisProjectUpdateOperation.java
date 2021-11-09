package com.webank.wedatasphere.exchangis.appconn.project;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisProjectUpdateOperation implements ProjectUpdateOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectUpdateOperation.class);
    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
