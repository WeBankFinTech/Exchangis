package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisProjectResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisEntityPutAction;
import com.webank.wedatasphere.exchangis.dss.appconn.request.entity.ProjectReqEntity;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Project update operation
 */
public class ExchangisProjectUpdateOperation extends AbstractExchangisProjectOperation implements ProjectUpdateOperation {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectUpdateOperation.class);

    private StructureService structureService;

    public ExchangisProjectUpdateOperation(StructureService structureService) {
        super(new String[]{"appProject"});
        setStructureService(structureService);
    }

    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        LOG.info("update project request => dss_projectId:{}, name:{}, createName:{}",
                projectRequestRef.getId(), projectRequestRef.getName(),projectRequestRef.getCreateBy());
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, Object>> entity = requestToGetEntity(projectRequestRef.getWorkspace(), projectRequestRef,
                (requestRef) -> {
                    // Build project put(update) action
                    return new ExchangisEntityPutAction<>(getProjectEntity(requestRef), requestRef.getCreateBy());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("update project response => status {}, response {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        AtomicLong newProjectId = new AtomicLong(projectRequestRef.getId());
        try {
            Optional.ofNullable(entity.getData()).ifPresent( data -> newProjectId
                    .set(Long.parseLong(String.valueOf(data.getOrDefault(Constraints.PROJECT_ID, newProjectId.get())))));
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Fail to resolve the project id from response entity", e);
        }
        ExchangisProjectResponseRef responseRef = new ExchangisProjectResponseRef(httpResult, newProjectId.get());
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
