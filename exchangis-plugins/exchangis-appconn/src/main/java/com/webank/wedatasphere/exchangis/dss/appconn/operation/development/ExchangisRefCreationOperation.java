package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;
import org.apache.linkis.httpclient.request.POSTAction;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;


/**
 * Ref creation operation
 */
public class ExchangisRefCreationOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.DSSJobContentRequestRefImpl, RefJobContentResponseRef>
        implements RefCreationOperation<ThirdlyRequestRef.DSSJobContentRequestRefImpl> {

    private String createUrl;

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        createUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/create"));
    }

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentRequestRefImpl createRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} try to create Exchangis job {} with jobContent: {}, refProjectId: {}, projectName: {}, nodeType:{}.",
                createRequestRef.getUserName(), createRequestRef.getName(), createRequestRef.getDSSJobContent(),
                createRequestRef.getRefProjectId(), createRequestRef.getProjectName(), createRequestRef.getType());
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(createRequestRef.getUserName());
        // TODO 创建工作流节点返回的projectid不正确
        addExchangisJobInfo(postAction, createRequestRef, createRequestRef.getRefProjectId());
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(createRequestRef, createUrl, postAction, ssoRequestOperation);
        return RefJobContentResponseRef.newBuilder().setRefJobContent(responseRef.getData()).success();
    }

    static void addExchangisJobInfo(POSTAction postAction, DSSJobContentRequestRef requestRef, Long refProjectId) {
        String desc = String.valueOf(requestRef.getDSSJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        postAction.addRequestPayload("projectId", refProjectId);
        postAction.addRequestPayload("jobType", Constraints.JOB_TYPE_OFFLINE);
        postAction.addRequestPayload("jobDesc", desc);
        postAction.addRequestPayload("jobName", requestRef.getName());
        postAction.addRequestPayload("source", MapUtils.newCommonMap("version",
                requestRef.getDSSJobContent().get(DSSJobContentConstant.ORC_VERSION_KEY),
                "workflowName", requestRef.getDSSJobContent().get(DSSJobContentConstant.ORCHESTRATION_NAME)));
        if(Constraints.NODE_TYPE_SQOOP.equalsIgnoreCase(requestRef.getType())){
            postAction.addRequestPayload("engineType", Constraints.ENGINE_TYPE_SQOOP_NAME);
        } else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(requestRef.getType())){
            postAction.addRequestPayload("engineType", Constraints.ENGINE_TYPE_DATAX_NAME);
        } else {
            throw new ExternalOperationFailedException(90512, "not supported Exchangis jobType " + requestRef.getType());
        }
    }
}
