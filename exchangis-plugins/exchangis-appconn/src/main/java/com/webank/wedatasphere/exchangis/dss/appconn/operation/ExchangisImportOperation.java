package com.webank.wedatasphere.exchangis.dss.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseRef;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExchangisImportOperation implements RefImportOperation<ImportRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisImportOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;

    public ExchangisImportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(Constraints.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef importRef(ImportRequestRef importRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + "/relation";
        logger.info("importRef job=>parameter:{} ||name:{}",importRequestRef.getParameters().toString(),importRequestRef.getName());

        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        exchangisPostAction.setUser(importRequestRef.getParameter("user").toString());

        exchangisPostAction.addRequestPayload("projectId", importRequestRef.getParameter("projectId"));
        exchangisPostAction.addRequestPayload("projectVersion", "v1");
        exchangisPostAction.addRequestPayload("flowVersion", importRequestRef.getParameter("orcVersion"));
        exchangisPostAction.addRequestPayload("resourceId", importRequestRef.getParameter("resourceId"));
        exchangisPostAction.addRequestPayload("version", importRequestRef.getParameter("version"));
        SSOUrlBuilderOperation ssoUrlBuilderOperation = importRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(Constraints.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(importRequestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
            logger.info("import job body:{}",responseRef.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31025, "import exchangis exception", e);
        }
        return responseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl() + Constraints.BASEURL;
    }
}
