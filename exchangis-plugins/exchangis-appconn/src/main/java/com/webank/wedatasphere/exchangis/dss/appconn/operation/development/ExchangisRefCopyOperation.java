package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import java.util.Map;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;
import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.REF_JOB_ID;


public class ExchangisRefCopyOperation extends
        AbstractDevelopmentOperation<ThirdlyRequestRef.CopyRequestRefImpl, RefJobContentResponseRef>
        implements RefCopyOperation<ThirdlyRequestRef.CopyRequestRefImpl> {

    private String copyUrl;

    @Override
    public void init() {
        super.init();
        copyUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/copy"));
    }

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyRequestRefImpl copyRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} try to copy Exchangis job {} with jobContent: {}, refProjectId: {}, projectName: {}, nodeType: {}.",
                copyRequestRef.getUserName(), copyRequestRef.getName(), copyRequestRef.getRefJobContent(),
                copyRequestRef.getProjectRefId(), copyRequestRef.getProjectName(), copyRequestRef.getType());
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(copyRequestRef.getUserName());
        postAction.addRequestPayload("projectId", copyRequestRef.getProjectRefId());
        postAction.addRequestPayload("partial", true);
        postAction.addRequestPayload("projectVersion", "v1");
        postAction.addRequestPayload("flowVersion", copyRequestRef.getNewVersion());
        String nodeType = copyRequestRef.getType();
        Long id = (Long) copyRequestRef.getRefJobContent().get(REF_JOB_ID);
        if(Constraints.NODE_TYPE_SQOOP.equals(nodeType)) {
            postAction.addRequestPayload("sqoopIds", id);
        } else if(Constraints.NODE_TYPE_DATAX.equalsIgnoreCase(nodeType)) {
            postAction.addRequestPayload("dataXIds", id);
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown Exchangis jobType " + nodeType);
        }
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(copyRequestRef, copyUrl, postAction, ssoRequestOperation);
        Map<Long, Long> sqoops = (Map<Long, Long>) responseRef.getData().get("sqoop");
        return RefJobContentResponseRef.newBuilder().setRefJobContent(MapUtils.newCommonMap(REF_JOB_ID, sqoops.get(id))).success();
    }

}
