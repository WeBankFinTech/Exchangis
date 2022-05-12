package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Ref update operation
 */
public class ExchangisRefUpdateOperation extends AbstractDevelopmentOperation<ThirdlyRequestRef.UpdateRequestRefImpl, ResponseRef>
        implements RefUpdateOperation<ThirdlyRequestRef.UpdateRequestRefImpl> {

    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateRequestRefImpl updateRequestRef) throws ExternalOperationFailedException {
        logger.info("User {} try to update Exchangis job {} with jobContent: {}, refProjectId: {}, projectName: {}, nodeType: {}.",
                updateRequestRef.getUserName(), updateRequestRef.getName(), updateRequestRef.getDSSJobContent(),
                updateRequestRef.getRefProjectId(), updateRequestRef.getProjectName(), updateRequestRef.getType());
        Integer id = (Integer) updateRequestRef.getDSSJobContent().get(Constraints.REF_JOB_ID);
        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "/appJob/" + id));
        DSSPutAction postAction = new DSSPutAction();
        postAction.setUser(updateRequestRef.getUserName());
        ExchangisRefCreationOperation.addExchangisJobInfo(postAction, updateRequestRef, updateRequestRef.getRefProjectId());
        postAction.addRequestPayload("id", id);
        InternalResponseRef responseRef = ExchangisHttpUtils.getResponseRef(updateRequestRef, url, postAction, ssoRequestOperation);
        return RefJobContentResponseRef.newBuilder().setRefJobContent(responseRef.getData()).success();
    }

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }
}
