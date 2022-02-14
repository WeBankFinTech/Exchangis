package com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc;

import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.httpclient.dws.request.DWSHttpAction;
import org.apache.linkis.httpclient.request.Action;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Operation contains request action
 */
public class LinkisDataSourceServiceOperation extends ServiceOperation {

    /**
     * Action
     */
    private Action requestAction;

    public LinkisDataSourceServiceOperation(Supplier<Action> actionBuilder){
        if (Objects.nonNull(actionBuilder)){
            requestAction = actionBuilder.get();
            if (requestAction instanceof DWSHttpAction){
                this.uri = (StringUtils.join(((DWSHttpAction) requestAction).suffixURLs(), "/"));
            }
        }
    }

    public Action getRequestAction() {
        return requestAction;
    }
}
