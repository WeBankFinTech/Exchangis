package com.webank.wedatasphere.exchangis.appconn.model;

import com.webank.wedatasphere.linkis.httpclient.request.GetAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

public class ExchangisGetAction extends GetAction implements UserAction {
    String url;
    String user;

    @Override
    public String getURL() {
        return url;
    }

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
