package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectDeletionOperation;
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
        return false;
    }

    @Override
    protected ProjectCreationOperation createProjectCreationOperation() {
        return new ExchangisProjectCreationOperation(this);
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return new ExchangisProjectUpdateOperation(this);
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return new ExchangisProjectDeletionOperation(this);
    }

    @Override
    protected ProjectGetOperation createProjectGetOperation() {
        return null;
    }

    @Override
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }
}
