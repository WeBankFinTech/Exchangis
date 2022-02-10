package com.webank.wedatasphere.exchangis.datasource.service.dispatch;

/**
 * Dispatch the operation
 */
public interface RemoteServiceDispatcher<T extends ServiceOperation> {

    /**
     * Dispatch entrance
     * @param operation
     */
    void dispatch(T operation);
}
