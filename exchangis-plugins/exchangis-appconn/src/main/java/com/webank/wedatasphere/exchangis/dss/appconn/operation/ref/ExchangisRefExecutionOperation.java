package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
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
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Ref execute operation
 */
public class ExchangisRefExecutionOperation extends AbstractExchangisRefOperation implements RefExecutionOperation {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefExecutionOperation.class);

    DevelopmentService developmentService;

    public ExchangisRefExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        setSSORequestService(service);
    }
    @Override
    public ResponseRef execute(ExecutionRequestRef executionRequestRef) throws ExternalOperationFailedException {
        AsyncExecutionRequestRef nodeRequestRef = (AsyncExecutionRequestRef) executionRequestRef;
        Long id = AppConnUtils.resolveParam(nodeRequestRef.getJobContent(), Constraints.REF_JOB_ID, Long.class);
        LOG.info("execute job request =>  id: {}, name: {}, user: {}, jobContent: {}",
                id, nodeRequestRef.getName(), nodeRequestRef.getExecutionRequestRefContext().getUser(),
                nodeRequestRef.getJobContent().toString());
        String url = requestURL("/job/execute/" + id);
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(nodeRequestRef.getWorkspace(), nodeRequestRef,
                (requestRef) -> {
                    // Build ref execution action
                    return new ExchangisEntityPostAction<>(null,
                            requestRef.getExecutionRequestRefContext().getUser());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("execute job response => status: {}, response: {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        return new ExchangisCommonResponseDef(httpResult);
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
