package com.webank.wedatasphere.exchangis.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;

import java.util.Map;

public class ExchangisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {
    private String errorMsg;

    private Long projectRefId;

    public ExchangisProjectResponseRef() {
        super("", 0);
    }

    protected ExchangisProjectResponseRef(String responseBody, int status, String errorMsg) {
        super(responseBody, status);
        this.errorMsg = errorMsg;
    }

    public Map<String, Object> toMap() {
        return null;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public Long getProjectRefId() {
        return this.projectRefId;
    }

    public Map<AppInstance, Long> getProjectRefIds() {
        return null;
    }

    public void setProjectRefId(Long projectRefId) {
        this.projectRefId = projectRefId;
    }
}
