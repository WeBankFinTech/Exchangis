package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.ExchangisPostAction;
import com.webank.wedatasphere.exchangis.dss.appconn.ref.AbstractExchangisResponseRef;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ref import operation
 */
public class ExchangisImportOperation extends AbstractExchangisRefOperation implements RefImportOperation<ImportRequestRef> {
    private final static Logger LOG = LoggerFactory.getLogger(ExchangisImportOperation.class);

    private DevelopmentService developmentService;

    public ExchangisImportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        setSSORequestService(this.developmentService);
    }

    @Override
    public ResponseRef importRef(ImportRequestRef importRequestRef) throws ExternalOperationFailedException {
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
