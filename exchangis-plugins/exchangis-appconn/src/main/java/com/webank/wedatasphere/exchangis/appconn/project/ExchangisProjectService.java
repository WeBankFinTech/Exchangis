package com.webank.wedatasphere.exchangis.appconn.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;

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
        return null;
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return null;
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    @Override
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }
}
