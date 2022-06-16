package com.webank.wedatasphere.exchangis.datasource.core.service.rpc;

/**
 * Each remote client should implement this interface
 */
public interface ServiceRpcClient<C> {

    default String getRemoteIp(){
        return "127.0.0.1";
    }

    C getClient();
}
