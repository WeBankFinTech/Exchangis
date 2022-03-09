package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisImportResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.AbstractExchangisResponseRef;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Ref import operation
 */
public class ExchangisImportOperation extends AbstractExchangisRefOperation implements RefImportOperation<ImportRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisImportOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public ExchangisImportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(this.developmentService);
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
    }

    @Override
    public ResponseRef importRef(ImportRequestRef importRequestRef) throws ExternalOperationFailedException {
        String url = developmentService.getAppInstance().getBaseUrl() + "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis/project" + "/import";
        ExchangisEntityPostAction exchangisEntityPostAction = new ExchangisEntityPostAction();
        exchangisEntityPostAction.setUser(importRequestRef.getParameter("user").toString());
        exchangisEntityPostAction.addRequestPayload("projectId", importRequestRef.getParameter("projectId"));
        exchangisEntityPostAction.addRequestPayload("projectVersion", "v1");
        exchangisEntityPostAction.addRequestPayload("flowVersion", importRequestRef.getParameter("orcVersion"));
        exchangisEntityPostAction.addRequestPayload("resourceId", importRequestRef.getParameter("version"));
        SSOUrlBuilderOperation ssoUrlBuilderOperation = importRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(importRequestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try {
            exchangisEntityPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisEntityPostAction);
            responseRef = new ExchangisImportResponseRef((Map<String, Object>) importRequestRef.getParameter("jobContent"), httpResult.getResponseBody(), importRequestRef.getParameter("nodeType").toString());
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Import Exchangis Exception", e);
        }
        return responseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
