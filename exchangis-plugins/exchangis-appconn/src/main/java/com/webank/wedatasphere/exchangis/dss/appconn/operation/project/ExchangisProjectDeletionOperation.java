package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.config.ExchangisConfig;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisDeleteAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisProjectResponseRef;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ExchangisProjectDeletionOperation implements ProjectDeletionOperation {
    private static Logger logger = LoggerFactory.getLogger(ExchangisProjectDeletionOperation.class);

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private StructureService structureService;

    public ExchangisProjectDeletionOperation(SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, StructureService structureService) {
        this.ssoRequestOperation = ssoRequestOperation;
        this.structureService = structureService;
    }

    private String getAppName() {
        return ExchangisConfig.EXCHANGIS_APPCONN_NAME;
    }
    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        Long projectId = projectRequestRef.getId();
        logger.info("delete project=>projectId:{},name:{},createName:{}",projectRequestRef.getId(),projectRequestRef.getName(),projectRequestRef.getCreateBy());

        String url = getBaseUrl() +"/projects/dss/"+projectRequestRef.getName();
        ExchangisDeleteAction exchangisPostAction = new ExchangisDeleteAction();
        exchangisPostAction.setUser(projectRequestRef.getCreateBy());

        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRequestRef.getWorkspace().getWorkspaceName());

        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            exchangisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, exchangisPostAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, new TypeReference<Map<String, Object>>() {});
        }catch (Exception e){
            logger.error("delete Exchangis Project Exception", e);
            throw new ExternalOperationFailedException(31020,e.getMessage());
        }

        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            throw new ExternalOperationFailedException(31020, errorMsg, null);
        }
        ExchangisProjectResponseRef exchangisProjectResponseRef = null;
        try {
            exchangisProjectResponseRef = new ExchangisProjectResponseRef(response, code);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31020, "failed to parse response json", e);
        }
        exchangisProjectResponseRef.setAppInstance(structureService.getAppInstance());
        exchangisProjectResponseRef.setProjectRefId(projectId);
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