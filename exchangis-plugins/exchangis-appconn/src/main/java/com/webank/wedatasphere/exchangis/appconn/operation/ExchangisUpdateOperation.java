package com.webank.wedatasphere.exchangis.appconn.operation;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateCSRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
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

public class ExchangisUpdateOperation implements RefUpdateOperation<UpdateRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisCreationOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;
    public ExchangisUpdateOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef updateRef(UpdateRequestRef updateRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef exchangisupdateRequestRef = (NodeRequestRef) updateRequestRef;
        logger.info("update job=>projectId:{},jobName:{},nodeType:{}",exchangisupdateRequestRef.getProjectId(),exchangisupdateRequestRef.getName(),exchangisupdateRequestRef.getNodeType());
        if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisupdateRequestRef.getNodeType())){
            updateSqoop(exchangisupdateRequestRef);
        }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisupdateRequestRef.getNodeType())){
            updateDataX(exchangisupdateRequestRef);
        }
        return null;
    }

    private ResponseRef updateSqoop(NodeRequestRef exchangisupdateRequestRef) throws ExternalOperationFailedException{

        logger.info("update sqoop job=>jobContent:{}",exchangisupdateRequestRef.getJobContent());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        String url="";
        try {
            String nodeId = AppconnUtils.getId(exchangisupdateRequestRef);
            long nodeIdl = Long.parseLong(nodeId);
            exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_ID,nodeIdl);
            url=getBaseUrl()+"/job/"+nodeIdl;
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
        }
        exchangisPostAction.setUser(exchangisupdateRequestRef.getUserName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_DESC,"");
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_LABELS, AppconnUtils.changeDssLabelName(exchangisupdateRequestRef.getDSSLabels()));
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_NAME,exchangisupdateRequestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_TYPE,ExchangisConfig.JOB_TYPE_OFFLINE);
        exchangisPostAction.addRequestPayload(ExchangisConfig.ENGINE_TYPE,ExchangisConfig.ENGINE_TYPE_SQOOP_NAME);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = exchangisupdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(exchangisupdateRequestRef.getWorkspace().getWorkspaceName());
        ExchangisCommonResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "update sqoop job Exception", e);
        }
        return responseRef;
    }

    private ResponseRef updateDataX(NodeRequestRef exchangisupdateRequestRef) throws ExternalOperationFailedException{
        logger.info("update datax job=>jobContent:{}",exchangisupdateRequestRef.getJobContent());
        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        String url="";
        try {
            String nodeId = AppconnUtils.getId(exchangisupdateRequestRef);
            long nodeIdl = Long.parseLong(nodeId);
            exchangisPostAction.addRequestPayload(ExchangisConfig.NODE_ID,nodeIdl);
            url=getBaseUrl()+"/job/"+nodeIdl;
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get node Id failed!", e);
        }
        exchangisPostAction.setUser(exchangisupdateRequestRef.getUserName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_DESC,"");
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_LABELS, AppconnUtils.changeDssLabelName(exchangisupdateRequestRef.getDSSLabels()));
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_NAME,exchangisupdateRequestRef.getName());
        exchangisPostAction.addRequestPayload(ExchangisConfig.JOB_TYPE,ExchangisConfig.JOB_TYPE_OFFLINE);
        exchangisPostAction.addRequestPayload(ExchangisConfig.ENGINE_TYPE,ExchangisConfig.ENGINE_TYPE_DATAX_NAME);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = exchangisupdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(exchangisupdateRequestRef.getWorkspace().getWorkspaceName());
        ExchangisCommonResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "update datax job Exception", e);
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
