package com.webank.wedatasphere.exchangis.appconn.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.linkis.httpclient.request.POSTAction;
import org.apache.linkis.httpclient.request.UserAction;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisPostAction extends POSTAction implements UserAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisPostAction.class);
    String url;
    String user;

    @Override
    public String getRequestPayload() {
        try {
            return BDPJettyServerHelper.jacksonJson().writeValueAsString(getRequestPayloads());
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to covert {} to a string", getRequestPayloads(), e);
            return "";
        }
    }

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
