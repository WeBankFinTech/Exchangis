package com.webank.wedatasphere.exchangis.appconn.operation;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DeleteRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisDeleteAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExchangisDeletionOperation implements RefDeletionOperation<DeleteRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisCreationOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;
    public ExchangisDeletionOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    public void deleteRef(DeleteRequestRef deleteRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef exchangisdeleteRequestRef = (NodeRequestRef) deleteRequestRef;
        logger.info("create job=>projectId:{},jobName:{},nodeType:{}",exchangisdeleteRequestRef.getProjectId(),exchangisdeleteRequestRef.getName(),exchangisdeleteRequestRef.getNodeType());

        if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisdeleteRequestRef.getNodeType())){
            deleteSqoop(exchangisdeleteRequestRef);
        }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisdeleteRequestRef.getNodeType())){
            deleteDataX(exchangisdeleteRequestRef);
        }
    }

    private void deleteSqoop(NodeRequestRef deleteRequestRef) throws ExternalOperationFailedException{
        String url=getBaseUrl()+"/job";
        logger.info("delete job=>projectId:{},jobName:{},nodeType:{}",deleteRequestRef.getProjectId(),deleteRequestRef.getName(),deleteRequestRef.getNodeType());


        ExchangisDeleteAction exchangisDeleteAction = new ExchangisDeleteAction();
        exchangisDeleteAction.setUser(deleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = deleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(deleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            exchangisDeleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisDeleteAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }

    }
    private void deleteDataX(NodeRequestRef deleteRequestRef) throws ExternalOperationFailedException{
        String url=getBaseUrl()+"/job";
        logger.info("delete job=>projectId:{},jobName:{},nodeType:{}",deleteRequestRef.getProjectId(),deleteRequestRef.getName(),deleteRequestRef.getNodeType());

        ExchangisDeleteAction exchangisDeleteAction = new ExchangisDeleteAction();
        exchangisDeleteAction.setUser(deleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = deleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(deleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            exchangisDeleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisDeleteAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl() + ExchangisConfig.BASEURL;
    }
}
