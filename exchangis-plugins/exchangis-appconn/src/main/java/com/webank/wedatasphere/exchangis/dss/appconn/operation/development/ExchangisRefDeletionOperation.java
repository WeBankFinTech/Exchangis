package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Ref delete operation
 */
public class ExchangisRefDeletionOperation extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ResponseRef>
        implements RefDeletionOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl deleteRequestRef) throws ExternalOperationFailedException {
        Integer id = (Integer) deleteRequestRef.getRefJobContent().get(Constraints.REF_JOB_ID);



        String url = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "appJob/" + id));
        logger.info("User {} try to delete Exchangis job {} in project {}, the url is {}.", deleteRequestRef.getUserName(),
                deleteRequestRef.getName(), deleteRequestRef.getProjectName(), url);
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(deleteRequestRef.getUserName());
        return ExchangisHttpUtils.getResponseRef(deleteRequestRef, url, postAction, ssoRequestOperation);
    }

}
