package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisCompletedExecutionResponseRef;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) executionRequestRef;
        String nodeType = asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("nodeType").toString();

        return new ExchangisCompletedExecutionResponseRef(200);
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
