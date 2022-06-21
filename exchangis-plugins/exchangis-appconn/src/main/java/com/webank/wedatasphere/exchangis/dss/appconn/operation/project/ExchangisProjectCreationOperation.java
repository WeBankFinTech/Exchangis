package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.httpclient.request.POSTAction;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.Map;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Project create operation
 */
public class ExchangisProjectCreationOperation extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private String projectUrl;


    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl projectRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} want to create a Exchangis project with dssProjectName:{}, createUser:{}, parameters:{}, workspaceName:{}",
                projectRequestRef.getUserName(), projectRequestRef.getDSSProject().getName(),
                projectRequestRef.getDSSProject().getCreateBy(), projectRequestRef.getParameters().toString(),
                projectRequestRef.getWorkspace().getWorkspaceName());
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(projectRequestRef.getUserName());
        addProjectInfo(postAction, projectRequestRef);
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(projectRequestRef, projectUrl, postAction, ssoRequestOperation);
        logger.info("User {} created a Exchangis project {} with response {}.", projectRequestRef.getUserName(), projectRequestRef.getDSSProject().getName(), responseRef.getResponseBody());
        long projectId;
        try {
            ResponseMessage data = BDPJettyServerHelper.gson().fromJson(responseRef.getResponseBody(), ResponseMessage.class);
            //responseRef.getResponseBody();
            //projectId = Long.parseLong(String.valueOf(responseRef.getData().get(Constraints.PROJECT_ID)));
            projectId = Long.parseLong(String.valueOf(data.getData().get("projectId")));
            logger.info("Exchangis projectId is {}", projectId);
        } catch (Exception e){
            throw new ExternalOperationFailedException(31020, "Fail to resolve the project id from response entity", e);
        }
        return ProjectResponseRef.newInternalBuilder().setRefProjectId(projectId).success();
    }

    @Override
    public void init() {
        super.init();
        projectUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appProject"));
    }

    public static void addProjectInfo(POSTAction postAction, DSSProjectContentRequestRef requestRef) {
        postAction.addRequestPayload("projectName", requestRef.getDSSProject().getName());
        postAction.addRequestPayload("description", requestRef.getDSSProject().getDescription());
        postAction.addRequestPayload("domain", Constraints.DOMAIN_NAME);
        postAction.addRequestPayload("source", MapUtils.newCommonMap("workspace", requestRef.getWorkspace().getWorkspaceName()));
        postAction.addRequestPayload("editUsers", StringUtils.join(requestRef.getDSSProjectPrivilege().getEditUsers(),","));
        postAction.addRequestPayload("viewUsers", StringUtils.join(requestRef.getDSSProjectPrivilege().getAccessUsers(),","));
        postAction.addRequestPayload("execUsers", StringUtils.join(requestRef.getDSSProjectPrivilege().getReleaseUsers(),","));
    }

    public static class ResponseMessage {
        private String method;
        private Double status;
        private String message;
        private Map<String, Object> data;

        public ResponseMessage() {
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Double getStatus() {
            return status;
        }

        public void setStatus(Double status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
