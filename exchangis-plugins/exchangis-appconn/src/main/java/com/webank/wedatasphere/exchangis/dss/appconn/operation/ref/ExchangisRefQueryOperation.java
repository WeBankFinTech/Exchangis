package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisOpenRequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.ExchangisOpenResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.AppConnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Ref query operation
 */
public class ExchangisRefQueryOperation extends AbstractExchangisRefOperation implements RefQueryOperation<OpenRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisRefQueryOperation.class);

    private DevelopmentService developmentService;

    public ExchangisRefQueryOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(developmentService);
    }
    @Override
    public ResponseRef query(OpenRequestRef openRequestRef) throws ExternalOperationFailedException {
        // Note: dss will scan the AppConn package to new the ExchangisOpenRequestRef
        ExchangisOpenRequestRef exchangisOpenRequestRef = (ExchangisOpenRequestRef) openRequestRef;
        try {
            Long id = AppConnUtils.resolveParam(exchangisOpenRequestRef.getJobContent(), Constraints.REF_JOB_ID, Long.class);
            String jumpUrl = requestURL(Constraints.REF_JUMP_URL_FORMAT + "?id=" + id);
            Map<String,String> retMap = new HashMap<>();
            LOG.info("ExchangisOpenResponseRef jump url: {}", jumpUrl);
            retMap.put("jumpUrl",jumpUrl);
            return new ExchangisOpenResponseRef(DSSCommonUtils.COMMON_GSON.toJson(retMap),0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(31022, "Failed to parse jobContent ", e);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
