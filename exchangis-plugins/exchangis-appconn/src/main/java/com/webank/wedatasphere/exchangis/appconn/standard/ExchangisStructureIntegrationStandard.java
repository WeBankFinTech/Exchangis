package com.webank.wedatasphere.exchangis.appconn.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.exchangis.appconn.service.ExchangisProjectService;

import java.io.IOException;

public class ExchangisStructureIntegrationStandard implements StructureIntegrationStandard {
    private static volatile ExchangisStructureIntegrationStandard instance;

    private AppConn appConn;

    private ExchangisStructureIntegrationStandard(AppConn appConn) {
        try {
            this.appConn = appConn;
            init();
        } catch (AppStandardErrorException e) {
            System.out.println("Failed to init {} " + getClass().getSimpleName());
        }
    }

    public static ExchangisStructureIntegrationStandard getInstance(AppConn appConn) {
        if (instance == null)
            synchronized (ExchangisStructureIntegrationStandard.class) {
                if (instance == null)
                    instance = new ExchangisStructureIntegrationStandard(appConn);
            }
        return instance;
    }

    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    public void setAppDesc(AppDesc appDesc) {}

    public void init() throws AppStandardErrorException {}

    public RoleService getRoleService() {
        return null;
    }

    public ProjectService getProjectService() {
        ExchangisProjectService service = new ExchangisProjectService();
        service.setAppDesc(getAppDesc());
        service.setAppStandard(this);
        return service;
    }

    public AppStatusService getAppStateService() {
        return null;
    }

    public void close() throws IOException {}
}
