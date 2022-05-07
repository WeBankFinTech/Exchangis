package com.webank.wedatasphere.exchangis.dss.appconn.request.action;


import org.apache.linkis.httpclient.request.GetAction;
import org.apache.linkis.httpclient.request.UserAction;

public class ExchangisGetAction extends GetAction implements HttpExtAction, UserAction {
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
