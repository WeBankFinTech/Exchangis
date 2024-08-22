package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.datasource.client.AbstractRemoteClient;
import org.apache.linkis.httpclient.dws.response.DWSResult;
import org.apache.linkis.httpclient.request.Action;
import org.apache.linkis.httpclient.response.Result;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Linkis data source service
 */
public abstract class AbstractLinkisDataSourceService {
    /**
     * Rpc send wrapper
     * @param client rpc client
     * @param decorator decorator for action
     * @param executor executor for client
     * @param <T> action type
     * @param <R> result type
     * @return result
     * @throws ExchangisDataSourceException e
     */
    protected  <T extends Action, R extends Result, C extends AbstractRemoteClient>R rpcSend(
            C client,
            Supplier<T> decorator, BiFunction<C, T, R> executor,
            int errorCode, String nonErrorMessage) throws ExchangisDataSourceException{
        T action = decorator.get();
        R result;
        try {
            result = executor.apply(client, action);
        } catch (ErrorException e) {
            // Deal with the linkis error exception
            throw new ExchangisDataSourceException(errorCode,
                    e.getMessage(), e.getIp(), e.getPort(), e.getServiceKind());
        } catch (Exception e){
            throw new ExchangisDataSourceException(errorCode, e.getMessage());
        }
        if (Objects.isNull(result) || StringUtils.isBlank(result.getResponseBody())){
            throw new ExchangisDataSourceException(errorCode, StringUtils.isNotBlank(nonErrorMessage) ? nonErrorMessage : "Empty linkis response");
        }
        if (result instanceof DWSResult) {
            DWSResult dwsResult = (DWSResult) result;
            if (dwsResult.getStatus() != 0){
                throw new ExchangisDataSourceException(dwsResult.getStatus(), dwsResult.getMessage());
            }
        }
        return result;
    }
}
