package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisPutAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCommonResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.AppConnUtils;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisUpdateOperation implements RefUpdateOperation<UpdateRequestRef> {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisUpdateOperation.class);

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
        ResponseRef responseRef = null;
        if(ExchangisConfig.NODE_TYPE_SQOOP.equalsIgnoreCase(exchangisupdateRequestRef.getNodeType())){
            responseRef = updateOffLineRequest(exchangisupdateRequestRef, ExchangisConfig.ENGINE_TYPE_SQOOP_NAME);
        }else if(ExchangisConfig.NODE_TYPE_DATAX.equalsIgnoreCase(exchangisupdateRequestRef.getNodeType())){
            responseRef = updateOffLineRequest(exchangisupdateRequestRef,ExchangisConfig.NODE_TYPE_DATAX);
        }
        return responseRef;
    }

    private ResponseRef updateOffLineRequest(NodeRequestRef exchangisupdateRequestRef,String engineType) throws ExternalOperationFailedException{
        logger.info("update sqoop job=>jobContent:{}",exchangisupdateRequestRef.getJobContent());
        ExchangisPutAction exchangisPutAction = new ExchangisPutAction();

        String nodeId = exchangisupdateRequestRef.getJobContent().get("nodeId").toString();
        String url=getBaseUrl()+"/dss/"+nodeId;

        exchangisPutAction.setUser(exchangisupdateRequestRef.getUserName());

        exchangisPutAction.setUser(exchangisupdateRequestRef.getUserName());
        exchangisPutAction.addRequestPayload(ExchangisConfig.NODE_NAME,exchangisupdateRequestRef.getName());
        exchangisPutAction.addRequestPayload(ExchangisConfig.JOB_DESC,exchangisupdateRequestRef.getJobContent().get("desc").toString());
        exchangisPutAction.addRequestPayload(ExchangisConfig.JOB_LABELS, AppConnUtils.changeDssLabelName(exchangisupdateRequestRef.getDSSLabels()));
        exchangisPutAction.addRequestPayload(ExchangisConfig.JOB_NAME,exchangisupdateRequestRef.getName());
        exchangisPutAction.addRequestPayload(ExchangisConfig.JOB_TYPE,ExchangisConfig.JOB_TYPE_OFFLINE);
        exchangisPutAction.addRequestPayload(ExchangisConfig.ENGINE_TYPE,engineType);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = exchangisupdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(exchangisupdateRequestRef.getWorkspace().getWorkspaceName());
        ExchangisCommonResponseRef responseRef;

        try{
            exchangisPutAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPutAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "update sqoop job Exception", e);
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
