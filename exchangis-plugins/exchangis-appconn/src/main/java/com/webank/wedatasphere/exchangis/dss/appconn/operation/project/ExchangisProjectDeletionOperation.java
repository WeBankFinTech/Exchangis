package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;


/**
 * Project delete operation
 */
public class ExchangisProjectDeletionOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRequestRef) throws ExternalOperationFailedException {
        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX,"appProject/" + projectRequestRef.getProjectName()));
        logger.info("User {} try to delete Exchangis project with refProjectId: {}, name: {}, the url is {}.", projectRequestRef.getUserName(),
                projectRequestRef.getRefProjectId(), projectRequestRef.getProjectName(), url);
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(projectRequestRef.getUserName());
        return ExchangisHttpUtils.getResponseRef(projectRequestRef, url, postAction, ssoRequestOperation);
    }

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }
}
