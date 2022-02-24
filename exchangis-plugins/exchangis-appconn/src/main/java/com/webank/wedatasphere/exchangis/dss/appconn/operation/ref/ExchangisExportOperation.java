package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ref export operation
 */
public class ExchangisExportOperation extends AbstractExchangisRefOperation implements RefExportOperation<ExportRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisExportOperation.class);

    private DevelopmentService developmentService;

    public ExchangisExportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(this.developmentService);
    }


    @Override
    public ResponseRef exportRef(ExportRequestRef exportRequestRef) throws ExternalOperationFailedException {
        return null;
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
