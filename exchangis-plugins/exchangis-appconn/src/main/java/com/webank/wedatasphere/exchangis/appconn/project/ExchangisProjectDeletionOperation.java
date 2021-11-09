package com.webank.wedatasphere.exchangis.appconn.project;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisProjectDeletionOperation implements ProjectDeletionOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectDeletionOperation.class);
    @Override
    public ResponseRef deleteProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
