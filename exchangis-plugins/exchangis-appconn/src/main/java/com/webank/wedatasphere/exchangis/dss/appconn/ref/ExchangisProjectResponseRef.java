package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Response ref
 */
public class ExchangisProjectResponseRef extends AbstractExchangisResponseRef implements ProjectResponseRef {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectResponseRef.class);
    private Long projectRefId;
    private AppInstance appInstance;

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
