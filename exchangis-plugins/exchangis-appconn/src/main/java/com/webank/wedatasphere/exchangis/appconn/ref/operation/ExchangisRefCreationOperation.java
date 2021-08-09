package com.webank.wedatasphere.exchangis.appconn.ref.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.appconn.ref.entity.ExchangisCreateRequestRef;
import com.webank.wedatasphere.exchangis.appconn.ref.entity.ExchangisCreateResponseRef;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;

public class ExchangisRefCreationOperation implements RefCreationOperation<CreateRequestRef, ResponseRef> {

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public ExchangisRefCreationOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
    }

    @Override
    public ResponseRef createRef(CreateRequestRef requestRef) throws ExternalOperationFailedException {
        ExchangisCreateRequestRef createRequestRef = (ExchangisCreateRequestRef) requestRef;
        createRequestRef.setParameter("example-param1", "nutsjian");
        createRequestRef.setParameter("example-param2", "32");
        createRequestRef.setParameter("example-param3", "male");
        return requestCall(createRequestRef);
//        return null;
    }

    // 同步请求三方系统
    private ResponseRef requestCall(ExchangisCreateRequestRef requestRef) throws ExternalOperationFailedException {

        String url = getBaseUrl() + "/development/create";

        System.out.println("requestCall => " + url);

        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
//        examplePostAction.setUser(requestRef.getUsername());
        exchangisPostAction.addRequestPayload("name", requestRef.getName());
        exchangisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
//        examplePostAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
//        if(requestRef.getJobContent().get("bindViewKey") != null){
//            String viewNodeName = requestRef.getJobContent().get("bindViewKey").toString();
//            if(StringUtils.isNotBlank(viewNodeName) && !"empty".equals(viewNodeName)){
//                viewNodeName = getNodeNameByKey(viewNodeName,(String) requestRef.getJobContent().get("json"));
//                visualisPostAction.addRequestPayload(CSCommonUtils.NODE_NAME_STR, viewNodeName);
//            }
//        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());

        System.out.println(ssoUrlBuilderOperation);

        ResponseRef responseRef;
        try{
            System.out.println("ssoUrlBuilderOperation builtUrl => " + ssoUrlBuilderOperation.getBuiltUrl());
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            //
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCreateResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Widget Exception", e);
        }
        // cs
//        VisualisRefUpdateOperation visualisRefUpdateOperation = new VisualisRefUpdateOperation(developmentService);
//        VisualisUpdateCSRequestRef visualisUpdateCSRequestRef = new VisualisUpdateCSRequestRef();
//        visualisUpdateCSRequestRef.setContextID((String) requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
//        visualisUpdateCSRequestRef.setJobContent(responseRef.toMap());
//        visualisUpdateCSRequestRef.setUserName(requestRef.getUsername());
//        visualisUpdateCSRequestRef.setNodeType(requestRef.getNodeType());
//        visualisUpdateCSRequestRef.setWorkspace(requestRef.getWorkspace());
//        visualisRefUpdateOperation.updateRef(visualisUpdateCSRequestRef);
        return responseRef;
    }

    private String getBaseUrl() {
        return this.developmentService.getAppInstance().getBaseUrl();
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
