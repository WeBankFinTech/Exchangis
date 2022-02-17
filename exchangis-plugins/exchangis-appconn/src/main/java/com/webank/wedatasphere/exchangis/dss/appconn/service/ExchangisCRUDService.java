package com.webank.wedatasphere.exchangis.dss.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisCreationOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisDeletionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ExchangisUpdateOperation;

public class ExchangisCRUDService  extends AbstractRefCRUDService {

    @Override
    protected RefCreationOperation createRefCreationOperation() {
        return new ExchangisCreationOperation(this);
    }


    @Override
    protected RefCopyOperation createRefCopyOperation() {
        return null;
    }

    @Override
    protected RefUpdateOperation createRefUpdateOperation() {
        return new ExchangisUpdateOperation(this);
    }

    @Override
    protected RefDeletionOperation createRefDeletionOperation() {
        return new ExchangisDeletionOperation(this);

    }
}
