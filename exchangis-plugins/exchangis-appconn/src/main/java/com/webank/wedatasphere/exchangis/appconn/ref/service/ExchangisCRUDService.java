package com.webank.wedatasphere.exchangis.appconn.ref.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.*;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.exchangis.appconn.ref.operation.ExchangisRefCreationOperation;

public class ExchangisCRUDService implements RefCRUDService {

    private DevelopmentService developmentService;

    @Override
    public DevelopmentService getDevelopmentService() {
        return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @Override
    public <K extends CreateRequestRef, V extends ResponseRef> RefCreationOperation<K, V> createTaskCreationOperation() {
        return (RefCreationOperation) new ExchangisRefCreationOperation(this.developmentService);
    }

    @Override
    public <K extends CopyRequestRef, V extends ResponseRef> RefCopyOperation<K, V> createRefCopyOperation() {
        return null;
    }

    @Override
    public <K extends RequestRef, V extends ResponseRef> RefUpdateOperation<K, V> createRefUpdateOperation() {
        return null;
    }

    @Override
    public <K extends DeleteRequestRef, V extends ResponseRef> RefDeletionOperation<K, V> createRefDeletionOperation() {
        return null;
    }
}
