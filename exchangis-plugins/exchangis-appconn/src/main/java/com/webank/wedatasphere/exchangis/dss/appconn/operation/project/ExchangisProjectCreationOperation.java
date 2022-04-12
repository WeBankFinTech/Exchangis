package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.AbstractExchangisOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisProjectResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.entity.ProjectReqEntity;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.JsonExtension;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Project create operation
 */
public class ExchangisProjectCreationOperation extends AbstractExchangisProjectOperation implements ProjectCreationOperation {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisProjectCreationOperation.class);

    private StructureService structureService;

    public ExchangisProjectCreationOperation(StructureService structureService) {
        super(new String[]{"appProject"});
        setStructureService(structureService);
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        LOG.info("create project request => dss_projectId:{}, name:{}, createUser:{}, parameters:{}, workspaceName:{}",
                projectRequestRef.getId(), projectRequestRef.getName(), projectRequestRef.getCreateBy(), projectRequestRef.getParameters().toString(),
                projectRequestRef.getWorkspace().getWorkspaceName());
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(projectRequestRef.getWorkspace(), projectRequestRef,
                (requestRef) -> {
                    // Build project post(add) action
                    ExchangisEntityPostAction<ProjectReqEntity> postAction =  new ExchangisEntityPostAction<>(getProjectEntity(requestRef));
                    postAction.setUser(requestRef.getCreateBy());
                    return postAction;
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("create project response => status {}, response {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        long projectId;
        try {
            projectId = Long.parseLong(String.valueOf(entity.getData().get(Constraints.PROJECT_ID)));
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Fail to resolve the project id from response entity", e);
        }
        ExchangisProjectResponseRef responseRef = new ExchangisProjectResponseRef(httpResult, projectId);
        responseRef.setAppInstance(structureService.getAppInstance());
        return responseRef;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService structureService) {
        this.structureService = structureService;
        setSSORequestService(this.structureService);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
