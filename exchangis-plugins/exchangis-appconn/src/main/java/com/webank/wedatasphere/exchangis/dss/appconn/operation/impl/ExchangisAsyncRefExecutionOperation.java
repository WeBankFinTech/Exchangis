package com.webank.wedatasphere.exchangis.dss.appconn.operation.impl;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Procedure;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ModuleFactory;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisCompletedExecutionResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisExecutionAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tikazhang
 * @Date 2022/4/28 11:38
 */
public class ExchangisAsyncRefExecutionOperation implements Procedure{

    private static final Logger logger = LoggerFactory.getLogger(ExchangisAsyncRefExecutionOperation.class);

    public ExchangisExecutionAction execute(ExecutionRequestRef requestRef, String baseUrl, SSORequestOperation ssoRequestOperation, DevelopmentService developmentService) throws ExternalOperationFailedException {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;
        ExchangisOptStrategy exchangisOpt = (ExchangisOptStrategy) ModuleFactory.getInstance().crateModule(Constraints.ENGINE_TYPE_SQOOP_NAME);
        String execId = exchangisOpt.submit(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, developmentService);
        ExchangisExecutionAction exchangisExecutionAction = new ExchangisExecutionAction();
        exchangisExecutionAction.set_execId(execId);
        exchangisExecutionAction.setRequestRef(requestRef);
        return exchangisExecutionAction;
    }

    public RefExecutionState state (RefExecutionAction action, String baseUrl, SSORequestOperation ssoRequestOperation) throws ExternalOperationFailedException {
        ExchangisExecutionAction sqoopAction = (ExchangisExecutionAction) action;
        ExecutionRequestRef requestRef = sqoopAction.getRequestRef();
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;
        String execId = sqoopAction.get_execId();
        ExchangisOptStrategy exchangisOpt = (ExchangisOptStrategy) ModuleFactory.getInstance().crateModule(Constraints.ENGINE_TYPE_SQOOP_NAME);
        RefExecutionState refExecutionState = exchangisOpt.state(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, execId);
        return refExecutionState;
    }

    public CompletedExecutionResponseRef result (RefExecutionAction action, String baseUrl, SSORequestOperation ssoRequestOperation) throws ExternalOperationFailedException {
        ExchangisExecutionAction sqoopAction = (ExchangisExecutionAction) action;
        ExecutionRequestRef requestRef = sqoopAction.getRequestRef();
        String execId = sqoopAction.get_execId();
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;
        ExchangisOptStrategy exchangisOpt = (ExchangisOptStrategy) ModuleFactory.getInstance().crateModule(Constraints.ENGINE_TYPE_SQOOP_NAME);
        exchangisOpt.getAsyncResult(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, execId);
        ExchangisCompletedExecutionResponseRef responseRef = new ExchangisCompletedExecutionResponseRef(200);
        responseRef.setIsSucceed(true);
        return responseRef;
    }

    public boolean kill(String baseUrl, SSORequestOperation ssoRequestOperation, RefExecutionAction refExecutionAction) {
        ExchangisExecutionAction sqoopAction = null;
        if (refExecutionAction instanceof ExchangisExecutionAction) {
            sqoopAction = (ExchangisExecutionAction) refExecutionAction;
        }
        ExecutionRequestRef requestRef = sqoopAction.getRequestRef();
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;
        try {
            ExchangisOptStrategy exchangisOpt = (ExchangisOptStrategy) ModuleFactory.getInstance().crateModule(Constraints.ENGINE_TYPE_SQOOP_NAME);
            if (exchangisOpt.kill(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, sqoopAction.get_execId())) {
                return true;
            } else {
                return false;
            }
        } catch (ExternalOperationFailedException e) {
            logger.info("kill executed job failed, execId is: {}", sqoopAction.get_execId());
        }
        return false;
    }

    @Override
    public float progress(RefExecutionAction refExecutionAction) {
        return 0;
    }

    @Override
    public String log(RefExecutionAction refExecutionAction) {
        return null;
    }
}
