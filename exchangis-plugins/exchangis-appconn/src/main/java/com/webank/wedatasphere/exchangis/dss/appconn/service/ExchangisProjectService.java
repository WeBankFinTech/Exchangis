package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectDeletionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectGetOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectUpdateOperation;

/**
 * Project service implement
 */
public class ExchangisProjectService extends ProjectService {

    @Override
    public boolean isCooperationSupported() {
        return true;
    }

    @Override
    public boolean isProjectNameUnique() {
        return true;
    }

    @Override
    protected ProjectCreationOperation createProjectCreationOperation() {
        return new ExchangisProjectCreationOperation();
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return new ExchangisProjectUpdateOperation();
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return new ExchangisProjectDeletionOperation();
    }

    @Override
    protected ProjectSearchOperation createProjectSearchOperation() {
        return new ExchangisProjectGetOperation();
    }
}
