package com.webank.wedatasphere.exchangis.appconn.sso;

import org.apache.http.client.methods.HttpPost;

public class ExchangisHttpPost extends HttpPost implements UserInfo {
    private String user;

    public ExchangisHttpPost(String url, String user) {
        super(url);
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }
}