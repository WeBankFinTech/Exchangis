package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
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


/**
 * Ref import operation
 */
public class ExchangisImportOperation extends AbstractDevelopmentOperation<ThirdlyRequestRef.ImportRequestRefImpl, RefJobContentResponseRef>
        implements RefImportOperation<ThirdlyRequestRef.ImportRequestRefImpl> {

    private String importUrl;

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        importUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/import"));
    }

    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportRequestRefImpl importRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} try to import Exchangis job {} with jobContent: {}, refProjectId: {}, projectName: {}, nodeType: {}.",
                importRequestRef.getUserName(), importRequestRef.getName(), importRequestRef.getRefJobContent(),
                importRequestRef.getRefProjectId(), importRequestRef.getProjectName(), importRequestRef.getType());
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(importRequestRef.getUserName());
        postAction.addRequestPayload("projectId", importRequestRef.getRefProjectId());
        postAction.addRequestPayload("projectVersion", "v1");
        postAction.addRequestPayload("flowVersion", importRequestRef.getNewVersion());
        postAction.addRequestPayload("resourceId", importRequestRef.getResourceMap().get(ImportRequestRef.RESOURCE_ID_KEY));
        postAction.addRequestPayload("version", importRequestRef.getResourceMap().get(ImportRequestRef.RESOURCE_VERSION_KEY));
        postAction.addRequestPayload("user", importRequestRef.getUserName());
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(importRequestRef, importUrl, postAction, ssoRequestOperation);
        Map<String, Object> realNode = (Map<String, Object>) responseRef.getData().get("sqoop");
        long newId = 0L;
        for (Map.Entry<String, Object> entry : realNode.entrySet()) {
            newId = ((Double) Double.parseDouble(entry.getValue().toString())).longValue();
            if (newId != 0) {
                break;
            }
        }
        logger.info("New job id is {}", newId);
        return RefJobContentResponseRef.newBuilder().setRefJobContent(MapUtils.newCommonMap(REF_JOB_ID, newId)).success();
    }

}
