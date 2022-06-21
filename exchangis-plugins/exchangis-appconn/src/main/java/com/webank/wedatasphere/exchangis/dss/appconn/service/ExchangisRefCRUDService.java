package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefCopyOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefDeletionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.development.ExchangisRefUpdateOperation;

/**
 * Ref CRUD service
 */
public class ExchangisRefCRUDService extends AbstractRefCRUDService {

    @Override
    protected ExchangisRefCreationOperation createRefCreationOperation() {
        return new ExchangisRefCreationOperation();
    }

    @Override
    protected ExchangisRefCopyOperation createRefCopyOperation() {
        return new ExchangisRefCopyOperation();
    }

    @Override
    protected ExchangisRefUpdateOperation createRefUpdateOperation() {
        return new ExchangisRefUpdateOperation();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ExchangisRefDeletionOperation createRefDeletionOperation() {
        return new ExchangisRefDeletionOperation();
    }
}
