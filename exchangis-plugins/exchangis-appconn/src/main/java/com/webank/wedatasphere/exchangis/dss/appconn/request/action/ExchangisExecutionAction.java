package com.webank.wedatasphere.exchangis.dss.appconn.request.action;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.LongTermRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;

/**
 * @author tikazhang
 * @Date 2022/4/29 16:25
 */
public class ExchangisExecutionAction extends AbstractRefExecutionAction implements LongTermRefExecutionAction {
    private RefExecutionState _state;
    private int schedulserId;
    private String _execId;
    private ExecutionRequestRef requestRef;

    public RefExecutionState get_state() {
        return _state;
    }

    public void set_state(RefExecutionState _state) {
        this._state = _state;
    }

    public String get_execId() {
        return _execId;
    }

    public void set_execId(String _execId) {
        this._execId = _execId;
    }

    public ExecutionRequestRef getRequestRef() {
        return requestRef;
    }

    public void setRequestRef(ExecutionRequestRef requestRef) {
        this.requestRef = requestRef;
    }

    @Override
    public void setSchedulerId(int schedulserId) {
        this.schedulserId = schedulserId;
    }

    @Override
    public int getSchedulerId() {
        return schedulserId;
    }
}
