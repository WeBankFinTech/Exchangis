package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.SuppressPackageLocation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Response ref
 */
public class ExchangisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectResponseRef.class);
    private Long projectRefId;
    private AppInstance appInstance;
    private String errorMsg;

    public ExchangisProjectResponseRef(ExchangisEntityRespResult result,
                                       Long projectId){
        super(result.getResponseBody(), result.getStatusCode());
        this.projectRefId = projectId;
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
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        try {
            responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        } catch (JsonProcessingException e) {
            LOG.warn("Fail to convert the response body {} to map", responseBody);
            return Collections.emptyMap();
        }
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
