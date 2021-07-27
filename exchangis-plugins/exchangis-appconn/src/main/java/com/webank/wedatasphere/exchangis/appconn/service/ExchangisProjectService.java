package com.webank.wedatasphere.exchangis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.exchangis.appconn.operation.ExchangisProjectCreationOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangisProjectService implements ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectService.class);

    private AppInstance appInstance;

    private StructureIntegrationStandard structureIntegrationStandard;

    private AppDesc appDesc;

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();

    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    public Operation createOperation(Class<? extends Operation> clazz) {
        return null;
    }

    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return false;
    }

    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return false;
    }

    public void setSSOService(AppIntegrationService ssoService) {}

    public AppIntegrationService getSSOService() {
        return null;
    }

    public void setAppStandard(StructureIntegrationStandard appStandard) {
        this.structureIntegrationStandard = appStandard;
    }

    public StructureIntegrationStandard getAppStandard() {
        return this.structureIntegrationStandard;
    }

    public AppInstance getAppInstance() {
        return this.appInstance;
    }

    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    public ProjectCreationOperation createProjectCreationOperation() {
        if (this.operationMap.containsKey(ExchangisProjectCreationOperation.class))
            return (ProjectCreationOperation)this.operationMap.get(ExchangisProjectCreationOperation.class);
        this.operationMap.put(ExchangisProjectCreationOperation.class, new ExchangisProjectCreationOperation(this));
        return (ProjectCreationOperation)this.operationMap.get(ExchangisProjectCreationOperation.class);
    }

    public ProjectUpdateOperation createProjectUpdateOperation() {
        return null;
    }

    public ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }
}
