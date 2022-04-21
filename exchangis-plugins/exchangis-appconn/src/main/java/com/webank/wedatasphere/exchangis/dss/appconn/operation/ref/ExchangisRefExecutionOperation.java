package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.*;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseDef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.AbstractExchangisResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import org.apache.commons.io.IOUtils;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Ref execute operation
 */
public class ExchangisRefExecutionOperation extends ExchangisLongTermRefExecutionOperation implements RefExecutionOperation {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefExecutionOperation.class);

    DevelopmentService developmentService;

    public ExchangisRefExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        setSSORequestService(service);
    }
    @Override
    public ResponseRef execute(ExecutionRequestRef executionRequestRef) {
        AsyncExecutionRequestRef nodeRequestRef = (AsyncExecutionRequestRef) executionRequestRef;

        LOG.info("execute job request =>  jobcontent: {}", nodeRequestRef.getJobContent());
        LOG.info("executionRequestRef =>  executionRequestRef: {}", executionRequestRef);
        try {
            nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString();
            LOG.info("getExecutionRequestRefContext User: {}", nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
        } catch (Exception e) {
            LOG.error("parsar r equest error", e);
        }
        Long id = AppConnUtils.resolveParam(nodeRequestRef.getJobContent(), Constraints.REF_JOB_ID, Double.class).longValue();
        LOG.info("execute job request =>  id: {}, name: {}, user: {}, jobContent: {}",
                id, nodeRequestRef.getName(), nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString(),
                nodeRequestRef.getJobContent().toString());
        String url = requestURL("/appJob/execute/" + id);
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = null;
        try {
            entity = requestToGetEntity(url, nodeRequestRef.getWorkspace(), nodeRequestRef,
                    (requestRef) -> {
                        // Build ref execution action
                        return new ExchangisEntityPostAction<>(null,
                                nodeRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
                    }, Map.class);
        } catch (ExternalOperationFailedException e) {
            e.printStackTrace();
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        AsyncExecutionResponseRef response = createAsyncResponseRef(executionRequestRef, (RefExecutionAction) executionRequestRef);
        return response;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }



    @Override
    protected RefExecutionAction submit(ExecutionRequestRef executionRequestRef) {
        LOG.info("submit 998.....");
        return null;
    }

    @Override
    public RefExecutionState state(RefExecutionAction refExecutionAction) {
        LOG.info("execute set state 111112");
        return null;
    }

    @Override
    public CompletedExecutionResponseRef result(RefExecutionAction refExecutionAction) {
        return null;
    }


}
