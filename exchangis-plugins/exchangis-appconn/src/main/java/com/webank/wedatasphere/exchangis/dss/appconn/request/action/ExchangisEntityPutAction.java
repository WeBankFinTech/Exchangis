package com.webank.wedatasphere.exchangis.dss.appconn.request.action;

import org.apache.linkis.httpclient.request.PutAction;
import org.apache.linkis.httpclient.request.UserAction;

/**
 * Actual contains a post action
 * @param <T>
 */
public class ExchangisEntityPutAction<T> extends PutAction implements HttpExtAction, UserAction {
    /**
     * Inner action
     */
    private ExchangisEntityPostAction<T> postAction;

    public ExchangisEntityPutAction(){
        postAction = new ExchangisEntityPostAction<>();
    }

    public ExchangisEntityPutAction(T entity){
        postAction = new ExchangisEntityPostAction<>(entity);
    }
    @Override
    public String getURL() {
        return postAction.getURL();
    }

    @Override
    public String getRequestPayload() {
        return postAction.getRequestPayload();
    }

    @Override
    public void setUser(String user) {
        postAction.setUser(user);
    }

    @Override
    public String getUser() {
        return postAction.getUser();
    }

    @Override
    public void setUrl(String url) {
        postAction.setUrl(url);
    }
}
