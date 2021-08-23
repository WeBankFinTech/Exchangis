package com.webank.wedatasphere.exchangis.appconn.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisPostAction extends POSTAction implements UserAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisPostAction.class);
    private String url;
    private String user;


    @Override
    public String getURL() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return this.user;
    }
}
