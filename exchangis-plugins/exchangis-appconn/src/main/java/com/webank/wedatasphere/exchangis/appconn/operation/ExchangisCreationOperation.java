package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCommonResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.AppconnUtils;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExchangisCreationOperation implements RefCreationOperation<CreateRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisCreationOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;
    public ExchangisCreationOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef createRef(CreateRequestRef createRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef exchangisCreateRequestRef = (NodeRequestRef) createRequestRef;
        logger.info("create job=>projectId:{},jobName:{},nodeType:{}",exchangisCreateRequestRef.getProjectId(),exchangisCreateRequestRef.getName(),exchangisCreateRequestRef.getNodeType());
        if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisCreateRequestRef.getNodeType())){
            sendSqoopRequest(exchangisCreateRequestRef);
        }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisCreateRequestRef.getNodeType())){
            sendDataXRequest(exchangisCreateRequestRef);
        }
        return null;
    }

    private ResponseRef sendSqoopRequest(NodeRequestRef requestRef)  throws ExternalOperationFailedException {
        String url = getBaseUrl()+"/job";
        logger.info("create sqoop job=>jobContent:{}",requestRef.getJobContent());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        try {
            String nodeId = AppconnUtils.getId(requestRef);
            exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_ID,Long.parseLong(nodeId));
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
        }

        exchangisPostAction.setUser(requestRef.getUserName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_ID,requestRef.getProjectId());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_DESC,"");
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_LABELS, AppconnUtils.changeDssLabelName(requestRef.getDSSLabels()));
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_TYPE,ExchangisConfig.JOB_TYPE_OFFLINE);
        exchangisPostAction.addRequestPayload(ExchangisConfig.ENGINE_TYPE,ExchangisConfig.ENGINE_TYPE_SQOOP_NAME);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        ExchangisCommonResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Create sqoop job Exception", e);
        }

        return responseRef;
    }

    private ResponseRef sendDataXRequest(NodeRequestRef requestRef)  throws ExternalOperationFailedException {
        String url=getBaseUrl()+"/job";
        logger.info("create dataX job=>jobContent:{}",requestRef.getJobContent());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        try {
            String nodeId = AppconnUtils.getId(requestRef);
            exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_ID,Long.parseLong(nodeId));
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
        }

        exchangisPostAction.setUser(requestRef.getUserName());
        exchangisPostAction.setUrl(url);
        exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_ID,requestRef.getProjectId());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_DESC,"");
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_LABELS, AppconnUtils.changeDssLabelName(requestRef.getDSSLabels()));
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_TYPE,ExchangisConfig.JOB_TYPE_OFFLINE);
        exchangisPostAction.addRequestPayload(ExchangisConfig.ENGINE_TYPE,ExchangisConfig.ENGINE_TYPE_DATAX_NAME);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        ExchangisCommonResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Create datax job Exception", e);
        }

        return responseRef;
    }


    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl() + ExchangisConfig.BASEURL;
    }
    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
