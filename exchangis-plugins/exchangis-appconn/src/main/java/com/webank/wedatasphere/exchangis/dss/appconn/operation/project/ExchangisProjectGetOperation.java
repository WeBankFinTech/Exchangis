package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectGetOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author tikazhang
 * @Date 2022/3/22 0:48
 */
public class ExchangisProjectGetOperation implements ProjectGetOperation {
    @Override
    public DSSProject getProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
