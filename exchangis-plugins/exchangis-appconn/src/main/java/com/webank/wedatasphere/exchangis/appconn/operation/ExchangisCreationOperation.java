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
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


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
        exchangisCreateRequestRef.getProjectName();


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
        logger.info("create sqoop job=>jobContent:{} ||,projectId:{} ||,projectName:{} ||,parameters:{} ||,type:{}",requestRef.getJobContent(),requestRef.getProjectId(),requestRef.getProjectName(),requestRef.getParameters().toString(),requestRef.getType());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        String projectName = null;
        try {
            String contextID = requestRef.getJobContent().get("contextID").toString();
            logger.info("contextID {}",contextID);

            Map contextIDMap =  BDPJettyServerHelper.jacksonJson().readValue(contextID, Map.class);
            logger.info("contextIDMap {}",contextIDMap.toString());

            String valueJson = contextIDMap.get("value").toString();
            logger.info("valueJson {}",valueJson);

            Map map = BDPJettyServerHelper.jacksonJson().readValue(valueJson, Map.class);
            logger.info("map {}",map.toString());
            projectName = map.get("project").toString();
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
        }

        exchangisPostAction.setUser(requestRef.getUserName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_ID,requestRef.getJobContent().get("projectId").toString());
        exchangisPostAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_NAME,projectName);
        exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_ID,requestRef.getJobContent().get("nodeId").toString());
        exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_DESC,requestRef.getJobContent().get("desc").toString());
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
            logger.info("sendSqoop => body:{}",httpResult.getResponseBody());

            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Create sqoop job Exception", e);
        }

        return responseRef;
    }

    private ResponseRef sendDataXRequest(NodeRequestRef requestRef)  throws ExternalOperationFailedException {
        String url=getBaseUrl()+"/job";

        logger.info("create datax job=>jobContent:{},projectId:{},projectName:{},parameters:{}",requestRef.getJobContent(),requestRef.getProjectId(),requestRef.getProjectName(),requestRef.getParameters().toString());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();

        exchangisPostAction.setUser(requestRef.getUserName());
        exchangisPostAction.setUrl(url);
        exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_NAME,requestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_ID,requestRef.getProjectId());
        exchangisPostAction.addRequestPayload(ExchangisConfig.PROJECT_NAME,requestRef.getProjectName());
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
            logger.info("sendSqoop => body:{}",httpResult.getResponseBody());
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
