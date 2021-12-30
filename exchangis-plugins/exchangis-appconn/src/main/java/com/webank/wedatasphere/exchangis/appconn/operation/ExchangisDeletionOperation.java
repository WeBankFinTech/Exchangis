package com.webank.wedatasphere.exchangis.appconn.operation;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DeleteRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisDeleteAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCommonResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.AppconnUtils;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExchangisDeletionOperation implements RefDeletionOperation {
    private final static Logger logger = LoggerFactory.getLogger(ExchangisDeletionOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;
    public ExchangisDeletionOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    public void deleteRef(RequestRef deleteRequestRef) throws ExternalOperationFailedException {
        NodeRequestRef nodeRequestRef = (NodeRequestRef) deleteRequestRef;
        logger.info("delete job=>jobContext:{}", nodeRequestRef.getJobContent().toString());
        deleteJob(nodeRequestRef);
    }

    private void deleteJob(NodeRequestRef deleteRequestRef) throws ExternalOperationFailedException{
        String url="";
        try {
            String jobId = ((Map<String,Object>)((Map<String,Object>) deleteRequestRef.getJobContent().get("data")).get("result")).get("id").toString();
            url = getBaseUrl()+"/"+jobId;
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get job Id failed!", e);
        }

        ExchangisDeleteAction exchangisDeleteAction = new ExchangisDeleteAction();
        exchangisDeleteAction.setUser(deleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = deleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        logger.info("delete job=>url:{}", url);
        ssoUrlBuilderOperation.setWorkspace(deleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            exchangisDeleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisDeleteAction);
            response = httpResult.getResponseBody();
            if(httpResult.getStatusCode() != 200){
                throw new ExternalOperationFailedException(31024, "Delete Job id error");
            }
            logger.info("delete => body:{}",response);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31023, "Delete Job Exception", e);
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
