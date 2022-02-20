package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisDeleteAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisProjectResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;


/**
 * Project delete operation
 */
public class ExchangisProjectDeletionOperation extends AbstractExchangisProjectOperation implements ProjectDeletionOperation {
    private static Logger LOG = LoggerFactory.getLogger(ExchangisProjectDeletionOperation.class);

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private StructureService structureService;

    public ExchangisProjectDeletionOperation(StructureService structureService) {
        setStructureService(structureService);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        // TODO Get the project id
        long projectId = 0l;
        String url = requestURL("/project/" + projectId);
        LOG.info("delete project request => dss_projectId:{}, name:{}, createName:{}", projectRequestRef.getId(),
                projectRequestRef.getName(), projectRequestRef.getCreateBy());
        ExchangisEntityRespResult.BasicMessageEntity<Map<String, String>> entity = requestToGetEntity(url, projectRequestRef.getWorkspace(), projectRequestRef,
                (requestRef) -> {
                    // Build project delete action
                    return new ExchangisDeleteAction(requestRef.getCreateBy());
                }, Map.class);
        if (Objects.isNull(entity)){
            throw new ExternalOperationFailedException(31020, "The response entity cannot be empty", null);
        }
        ExchangisEntityRespResult httpResult = entity.getResult();
        LOG.info("delete project response => status {}, response {}", httpResult.getStatusCode(), httpResult.getResponseBody());
        ExchangisProjectResponseRef responseRef = new ExchangisProjectResponseRef(httpResult, null);
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
}
