package com.webank.wedatasphere.exchangis.dss.appconn.operation.development;

import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.ExchangisHttpUtils;

/**
 * Ref query operation
 */
public class ExchangisRefQueryOperation extends
        AbstractDevelopmentOperation<ThirdlyRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef>
        implements RefQueryJumpUrlOperation<ThirdlyRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef> {

    @Override
    protected String getAppConnName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    @Override
    public QueryJumpUrlResponseRef query(ThirdlyRequestRef.QueryJumpUrlRequestRefImpl openRequestRef) throws ExternalOperationFailedException {
        Integer id = (Integer) openRequestRef.getRefJobContent().get(Constraints.REF_JOB_ID);
        //String labels = ExchangisHttpUtils.serializeDssLabel(openRequestRef.getDSSLabels());
        String labels = openRequestRef.getDSSLabels().get(0).getValue().get("DSSEnv");
        String jumpUrl = mergeBaseUrl(Constraints.REF_JUMP_URL_FORMAT + "?id=" + id + "&labels=" + labels);
        return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(jumpUrl).success();
    }

}
