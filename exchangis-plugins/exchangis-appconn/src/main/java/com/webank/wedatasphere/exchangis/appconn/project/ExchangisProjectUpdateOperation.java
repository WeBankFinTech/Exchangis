package com.webank.wedatasphere.exchangis.appconn.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.appconn.model.ExchangisPutAction;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisProjectResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.AppconnUtils;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.manager.label.entity.SerializableLabel;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExchangisProjectUpdateOperation implements ProjectUpdateOperation {
    private static final Logger logger = LoggerFactory.getLogger(ExchangisProjectUpdateOperation.class);
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private StructureService structureService;

    public ExchangisProjectUpdateOperation(SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, StructureService structureService) {
        this.ssoRequestOperation = ssoRequestOperation;
        this.structureService = structureService;
    }

    private String getAppName() {
        return ExchangisConfig.EXCHANGIS_APPCONN_NAME;
    }
    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() +"/updateProject";
        logger.info("update project=>projectId:{},name:{},createName:{}",projectRequestRef.getId(),projectRequestRef.getName(),projectRequestRef.getCreateBy());

        ExchangisPutAction exchangisPutAction = new ExchangisPutAction();
        exchangisPutAction.setUser(projectRequestRef.getCreateBy());
        exchangisPutAction.addRequestPayload(ExchangisConfig.WORKSPACE_NAME,projectRequestRef.getWorkspaceName());
        exchangisPutAction.addRequestPayload(ExchangisConfig.DSS_PROJECT_ID,projectRequestRef.getId());
        exchangisPutAction.addRequestPayload(ExchangisConfig.PROJECT_NAME,projectRequestRef.getName());
        exchangisPutAction.addRequestPayload(ExchangisConfig.DESCRIPTION,projectRequestRef.getDescription());
        exchangisPutAction.addRequestPayload(ExchangisConfig.EDIT_USERS,projectRequestRef.getUpdateBy());
        exchangisPutAction.addRequestPayload(ExchangisConfig.EXEC_USERS,projectRequestRef.getUpdateBy());
        exchangisPutAction.addRequestPayload(ExchangisConfig.VIEW_USERS,projectRequestRef.getUpdateBy());
        exchangisPutAction.addRequestPayload(ExchangisConfig.TAGS, AppconnUtils.changeDssLabelName(projectRequestRef.getDSSLabels()));

        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRequestRef.getWorkspace().getWorkspaceName());

        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            exchangisPutAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPutAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, new TypeReference<Map<String, Object>>() {});
        }catch (Exception e){
            logger.error("Create Exchangis Project Exception", e);
            throw new ExternalOperationFailedException(31020,e.getMessage());
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            throw new ExternalOperationFailedException(31020, errorMsg, null);
        }
        Integer projectId = (Integer) ((Map<String, Object>) resMap.get("payload")).get("projectId");
        ExchangisProjectResponseRef exchangisProjectResponseRef = null;
        try {
            exchangisProjectResponseRef = new ExchangisProjectResponseRef(response, code);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31020, "failed to parse response json", e);
        }
        exchangisProjectResponseRef.setAppInstance(structureService.getAppInstance());
        exchangisProjectResponseRef.setProjectRefId(projectId.longValue());
        exchangisProjectResponseRef.setErrorMsg(errorMsg);
        return exchangisProjectResponseRef;
    }

    @Override
    public void init() {

    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl() + ExchangisConfig.BASEURL;
    }
    @Override
    public void setStructureService(StructureService structureService) {
        this.structureService=structureService;
    }
}
