package com.webank.wedatasphere.exchangis.appconn.ref;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExchangisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectResponseRef.class);
    private Long projectRefId;
    private AppInstance appInstance;
    private String errorMsg;

    public ExchangisProjectResponseRef(String responseBody, int status) throws Exception {
        super(responseBody, status);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
    }

    @Override
    public Long getProjectRefId() {
        return projectRefId;
    }

    @Override
    public Map<AppInstance, Long> getProjectRefIds() {
        Map<AppInstance, Long> projectRefIdsMap = Maps.newHashMap();
        projectRefIdsMap.put(appInstance, projectRefId);
        return projectRefIdsMap;
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setProjectRefId(Long projectRefId) {
        this.projectRefId = projectRefId;
    }

    public AppInstance getAppInstance() {
        return appInstance;
    }

    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
