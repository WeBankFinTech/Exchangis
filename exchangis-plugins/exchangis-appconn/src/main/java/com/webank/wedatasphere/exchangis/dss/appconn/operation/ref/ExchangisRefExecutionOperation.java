package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.*;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.SchedulerManager;
import com.webank.wedatasphere.dss.standard.app.development.listener.scheduler.LongTermRefExecutionScheduler;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.impl.ExchangisAsyncRefExecutionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.impl.ExchangisOptStrategy;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCommonResponseDef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisExecutionAction;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Ref execute operation
 */
public class ExchangisRefExecutionOperation extends LongTermRefExecutionOperation implements RefExecutionOperation, Killable {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefExecutionOperation.class);

    DevelopmentService developmentService;

    SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    LongTermRefExecutionScheduler scheduler = SchedulerManager.getScheduler();

    public ExchangisRefExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(Constraints.EXCHANGIS_APPCONN_NAME);
    }

    @Override
    protected RefExecutionAction submit(ExecutionRequestRef executionRequestRef) {
        try {
            return new ExchangisAsyncRefExecutionOperation().execute(executionRequestRef, developmentService.getAppInstance().getBaseUrl(), ssoRequestOperation, developmentService);
        } catch (ExternalOperationFailedException e) {
            LOG.error("submit execute job failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public RefExecutionState state(RefExecutionAction refExecutionAction) {
        try {
            return new ExchangisAsyncRefExecutionOperation().state(refExecutionAction, developmentService.getAppInstance().getBaseUrl(), ssoRequestOperation);
        } catch (ExternalOperationFailedException e) {
            LOG.error("getting execute job state failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public CompletedExecutionResponseRef result(RefExecutionAction refExecutionAction) {
        try {
            return new ExchangisAsyncRefExecutionOperation().result(refExecutionAction, developmentService.getAppInstance().getBaseUrl(), ssoRequestOperation);
        } catch (ExternalOperationFailedException e) {
            LOG.error("getting execute job resulted failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public ResponseRef execute(ExecutionRequestRef executionRequestRef) {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) executionRequestRef;
        String nodeType = asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("nodeType").toString().toLowerCase().trim();
        LOG.info("nodeType:{}", nodeType);
        String nodeName = Constraints.ENGINE_TYPE_SQOOP_NAME;
        LOG.info("nodeName:{}", nodeName);
        if (Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase("linkis.appconn." + nodeType)) {
            try {
                return executeAsyncOpt(asyncExecutionRequestRef);
            } catch (ExternalOperationFailedException e) {
                asyncExecutionRequestRef.getExecutionRequestRefContext().appendLog(e.getMessage());
                LOG.error("Async execute sqoop node failed", e);
            }
        } else {
            return executeSyncOpt(executionRequestRef, nodeName);
        }
        return null;
    }

    /**
     * 执行异步操作
     *
     * @param requestRef
     * @return
     */
    private ResponseRef executeAsyncOpt(ExecutionRequestRef requestRef) throws ExternalOperationFailedException {

        ExchangisOptStrategy sqoopStrategy = (ExchangisOptStrategy) ModuleFactory.getInstance().crateModule(Constraints.ENGINE_TYPE_SQOOP_NAME);
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;

        //任务发布
        RefExecutionAction action = submit(requestRef);
        ExchangisExecutionAction sqoopAction = null;
        if (action instanceof ExchangisExecutionAction) {
            sqoopAction = (ExchangisExecutionAction) action;
        }
        if (action instanceof AbstractRefExecutionAction) {
            ((AbstractRefExecutionAction) action).setExecutionRequestRefContext(createExecutionRequestRefContext(requestRef));
        }
        //获取任务状态
        RefExecutionState state = state(sqoopAction);
        LOG.info("Now state is: {}", state.getStatus());
        if (state != null && state.isCompleted()) {
            //获取结果集
            CompletedExecutionResponseRef response = result(sqoopAction);
            return response;
        } else {
            AsyncExecutionResponseRef response = createAsyncResponseRef(requestRef, action);
            response.setRefExecution(this);
            if (scheduler != null) {
                scheduler.addAsyncResponse(response);
            }
            return response;
        }

    }

    /**
     * 执行同步操作
     *
     * @param ref
     * @param nodeName
     * @return
     */
    private ResponseRef executeSyncOpt(ExecutionRequestRef ref, String nodeName) {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) ref;
        try {
            return ModuleFactory.getInstance().crateModule(nodeName).executeRef(asyncExecutionRequestRef, developmentService.getAppInstance().getBaseUrl(), ssoRequestOperation);
        } catch (ExternalOperationFailedException e) {
            LOG.error("execute " + nodeName + " failed", e);
        }
        return null;

    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    public boolean kill(RefExecutionAction refExecutionAction) {
        return new ExchangisAsyncRefExecutionOperation().kill(developmentService.getAppInstance().getBaseUrl(), ssoRequestOperation, refExecutionAction);
    }
}
