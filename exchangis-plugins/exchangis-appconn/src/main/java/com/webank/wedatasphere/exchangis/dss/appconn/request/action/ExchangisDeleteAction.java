package com.webank.wedatasphere.exchangis.dss.appconn.request.action;


import org.apache.linkis.httpclient.request.DeleteAction;
import org.apache.linkis.httpclient.request.UserAction;

/**
 * Delete action
 */
public class ExchangisDeleteAction extends DeleteAction implements HttpExtAction, UserAction {

    /**
     * URL
     */
    String url;

    String user;

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }
}
