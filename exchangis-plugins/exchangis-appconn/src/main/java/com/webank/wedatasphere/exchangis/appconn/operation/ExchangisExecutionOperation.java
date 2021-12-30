package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCommonResponseRef;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCompletedExecutionResponseRef;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExchangisExecutionOperation implements RefExecutionOperation {

    private final static Logger logger = LoggerFactory.getLogger(ExchangisExecutionOperation.class);
    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public ExchangisExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
    }
    @Override
    public ResponseRef execute(ExecutionRequestRef executionRequestRef) throws ExternalOperationFailedException {
        AsyncExecutionRequestRef nodeRequestRef = (AsyncExecutionRequestRef) executionRequestRef;
        logger.info("execution name:{} || jobContext:{} || RuntimeMap:{} || user:{}",nodeRequestRef.getName(),nodeRequestRef.getJobContent().toString(),nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().toString(),nodeRequestRef.getExecutionRequestRefContext().getUser());
        String url="";
        try {
            String jobId = ((Map<String,Object>)((Map<String,Object>) nodeRequestRef.getJobContent().get("data")).get("result")).get("id").toString();
            url +="/"+jobId+"/action/execute";
        }catch (Exception e){
            throw new ExternalOperationFailedException(31023, "Get job Id failed!", e);
        }

        ExchangisPostAction exchangisPostAction = new ExchangisPostAction();
        exchangisPostAction.setUser(getUser(nodeRequestRef));
        SSOUrlBuilderOperation ssoUrlBuilderOperation = nodeRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(ExchangisConfig.EXCHANGIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(nodeRequestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try{
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            responseRef = new ExchangisCommonResponseRef(httpResult.getResponseBody());
            logger.info("execute job body:{}",responseRef.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(31025, "import exchangis exception", e);
        }

        return responseRef;
    }
    private String getUser(AsyncExecutionRequestRef requestRef) {
        return requestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString();
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
