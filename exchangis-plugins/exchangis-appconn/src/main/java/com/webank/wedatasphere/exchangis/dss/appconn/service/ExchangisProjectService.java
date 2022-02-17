package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.exchangis.dss.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectDeletionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.project.ExchangisProjectUpdateOperation;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;

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

        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ExchangisProjectCreationOperation exchangisProjectCreationOperation = new ExchangisProjectCreationOperation(this);
        exchangisProjectCreationOperation.setStructureService(this);
        return exchangisProjectCreationOperation;
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ExchangisProjectUpdateOperation exchangisProjectUpdateOperation = new ExchangisProjectUpdateOperation(ssoRequestOperation,this);
        exchangisProjectUpdateOperation.setStructureService(this);
        return exchangisProjectUpdateOperation;
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {

        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ExchangisProjectDeletionOperation exchangisProjectDeletionOperation = new ExchangisProjectDeletionOperation(ssoRequestOperation,this);
        exchangisProjectDeletionOperation.setStructureService(this);
        return exchangisProjectDeletionOperation;
    }

    @Override
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }
}
