package com.webank.wedatasphere.exchangis.dss.appconn.operation.project;

import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import java.util.Map;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * @author tikazhang
 * @Date 2022/3/22 0:48
 */
public class ExchangisProjectGetOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectSearchOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRequestRef) throws ExternalOperationFailedException {

        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appProject/check/" + projectRequestRef.getProjectName()));
        logger.info("User {} try to search Exchangis project with name: {}, the url is {}.", projectRequestRef.getUserName(),
                projectRequestRef.getProjectName(), url);
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(projectRequestRef.getUserName());
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(projectRequestRef, url, postAction, ssoRequestOperation);
        Map<String, Object> projectInfo = (Map<String, Object>) responseRef.getData().get("projectInfo");
        //如果查询不到project，则无重复项目可以直接返回
        if (projectInfo == null) {
            return ProjectResponseRef.newInternalBuilder().success();
        } else {
            return ProjectResponseRef.newInternalBuilder().setRefProjectId(Long.parseLong(projectInfo.get("id").toString())).success();
        }
    }

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

}