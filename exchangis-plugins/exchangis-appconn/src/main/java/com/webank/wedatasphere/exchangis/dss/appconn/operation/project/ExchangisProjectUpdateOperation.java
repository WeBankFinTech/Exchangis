package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Project update operation
 */
public class ExchangisProjectUpdateOperation
        extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {

    private String projectUpdateUrl;

    @Override
    public ResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRequestRef) throws ExternalOperationFailedException {
        String url = mergeUrl(projectUpdateUrl, String.valueOf(projectRequestRef.getRefProjectId()));
        logger.info("User {} try to update Exchangis project with dssProjectName: {}, refProjectId: {}, url is {}.",
                projectRequestRef.getUserName(), projectRequestRef.getDSSProject().getName(),
                projectRequestRef.getRefProjectId(), url);
        DSSPutAction putAction = new DSSPutAction();
        putAction.setUser(projectRequestRef.getUserName());
        addProjectInfo(putAction, projectRequestRef);
        logger.info("project payload is: {}", putAction.getRequestPayload());
        ExchangisProjectCreationOperation.addProjectInfo(putAction, projectRequestRef);
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(projectRequestRef, url, putAction, ssoRequestOperation);
        logger.info("User {} updated Exchangis project {} with response {}.", projectRequestRef.getUserName(), projectRequestRef.getRefProjectId(), responseRef.getResponseBody());
        return responseRef;
    }

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        projectUpdateUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appProject"));
    }

    public static void addProjectInfo(DSSPutAction putAction, ProjectUpdateRequestRef requestRef) {
        putAction.addRequestPayload("id", requestRef.getRefProjectId());
        putAction.addRequestPayload("projectName", requestRef.getDSSProject().getName());
        putAction.addRequestPayload("description", requestRef.getDSSProject().getDescription());
        putAction.addRequestPayload("domain", Constraints.DOMAIN_NAME);
        putAction.addRequestPayload("source", MapUtils.newCommonMap("workspace", requestRef.getWorkspace().getWorkspaceName()));
    }

}
