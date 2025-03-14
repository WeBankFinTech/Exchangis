package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Ref execute operation
 */
public class ExchangisRefExecutionOperation
        extends LongTermRefExecutionOperation<RefExecutionRequestRef.RefExecutionProjectRequestRef> implements Killable {

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    protected RefExecutionAction submit(RefExecutionRequestRef.RefExecutionProjectRequestRef executionRequestRef) {
        String execUser = executionRequestRef.getExecutionRequestRefContext().getUser();
        String submitUser = executionRequestRef.getExecutionRequestRefContext().getSubmitUser();
        logger.info("User {} try to execute Exchangis job {} in execute user: [{}] with jobContent: {}, refProjectId: {}, projectName: {}, nodeType:{}.",
                submitUser, executionRequestRef.getName(), execUser, executionRequestRef.getRefJobContent(),
                executionRequestRef.getRefProjectId(), executionRequestRef.getProjectName(), executionRequestRef.getType());
        long id = ((Double) executionRequestRef.getRefJobContent().get(Constraints.REF_JOB_ID)).longValue();
        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/execute/" + id));
        executionRequestRef.getExecutionRequestRefContext().appendLog("try to execute " + executionRequestRef.getType() + " node, ready to request to " + url);
        DSSPostAction postAction = new DSSPostAction();
        // Use submit user to login
        postAction.setUser(submitUser);
        // Add exec user to request body
        postAction.addRequestPayload("execUser", execUser);
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(executionRequestRef, url, postAction, ssoRequestOperation);
        ExchangisExecutionAction action = new ExchangisExecutionAction();
        action.setExecId((String) responseRef.getData().get("jobExecutionId"));
        action.setRequestRef(executionRequestRef);
        executionRequestRef.getExecutionRequestRefContext().appendLog("Submitted to Exchangis with execId: [" + action.getExecId() + "]");
        return action;
    }

    @Override
    public RefExecutionState state(RefExecutionAction refExecutionAction) {
        ExchangisExecutionAction action = (ExchangisExecutionAction) refExecutionAction;
        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "job/execution/" + action.getExecId() +"/status"));
        DSSGetAction getAction = new DSSGetAction();
        getAction.setUser(action.getExecutionRequestRefContext().getSubmitUser());
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(action.getRequestRef(), url, getAction, ssoRequestOperation);
        String status = (String) responseRef.getData().get("status");
        action.getExecutionRequestRefContext().appendLog("ExchangisJob(execId: " + action.getExecId() + ") is in state " + status);
        switch (status) {
            case "Failed":
            case "Timeout":
                return RefExecutionState.Failed;
            case "Success":
                return RefExecutionState.Success;
            case "Cancelled":
                return RefExecutionState.Killed;
            default:
                return RefExecutionState.Running;
        }
    }

    @Override
    public ExecutionResponseRef result(RefExecutionAction refExecutionAction) {
        RefExecutionState state = state(refExecutionAction);
        if(state.isSuccess()) {
            return ExecutionResponseRef.newBuilder().success();
        } else {
            // TODO 补充详细错误信息
            return ExecutionResponseRef.newBuilder().error("Please jump into Exchangis for more detail errors.");
        }
    }

    @Override
    public boolean kill(RefExecutionAction refExecutionAction) {
        // Invoke kill method
        ExchangisExecutionAction action = (ExchangisExecutionAction) refExecutionAction;
        action.getExecutionRequestRefContext().appendLog("try to kill ExchangisJob with execId: " + action.getExecId());
        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "job/execution/" + action.getExecId() +"/kill"));
        DSSPostAction postAction = new DSSPostAction();
        // Use submit user
        postAction.setUser(action.getExecutionRequestRefContext().getSubmitUser());
        ExchangisHttpUtils.getResponseRef(action.getRequestRef(), url, postAction, ssoRequestOperation);
        return true;
    }

    static class ExchangisExecutionAction extends AbstractRefExecutionAction {

        private String execId;
        private RefExecutionRequestRef.RefExecutionProjectRequestRef requestRef;

        public String getExecId() {
            return execId;
        }

        public void setExecId(String _execId) {
            this.execId = _execId;
        }

        public RefExecutionRequestRef.RefExecutionProjectRequestRef getRequestRef() {
            return requestRef;
        }

        public void setRequestRef(RefExecutionRequestRef.RefExecutionProjectRequestRef requestRef) {
            this.requestRef = requestRef;
        }

    }
}
