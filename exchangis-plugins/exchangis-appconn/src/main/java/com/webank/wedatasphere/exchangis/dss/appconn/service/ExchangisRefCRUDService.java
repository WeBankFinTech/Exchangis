package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.DeleteRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefCopyOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefDeletionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefUpdateOperation;

/**
 * Ref CRUD service
 */
public class ExchangisRefCRUDService extends AbstractRefCRUDService {

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends CreateRequestRef> RefCreationOperation<K> createRefCreationOperation() {
        return (RefCreationOperation<K>) new ExchangisRefCreationOperation(this);
    }

    @Override
    protected <K extends CopyRequestRef> RefCopyOperation<K> createRefCopyOperation() {
        return (RefCopyOperation<K>) new ExchangisRefCopyOperation(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends RequestRef> RefUpdateOperation<K> createRefUpdateOperation() {
        return (RefUpdateOperation<K>) new ExchangisRefUpdateOperation(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends DeleteRequestRef> RefDeletionOperation<K> createRefDeletionOperation() {
        return new ExchangisRefDeletionOperation(this);
    }
}
