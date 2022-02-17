package com.webank.wedatasphere.exchangis.dss.appconn.request.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.linkis.httpclient.request.POSTAction;
import org.apache.linkis.httpclient.request.UserAction;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Entity post action
 * @param <T>
 */
public class ExchangisEntityPostAction<T> extends POSTAction implements HttpExtAction, UserAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisEntityPostAction.class);

    /**
     * URL
     */
    private String url;

    private String user;
    /**
     * Entity to post request
     */
    private T postEntity;

    public ExchangisEntityPostAction(){

    }

    public ExchangisEntityPostAction(T postEntity){
        this.postEntity = postEntity;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getRequestPayload() {
        String requestPayLoad = "";
        try {
            if (Objects.nonNull(postEntity)) {
                requestPayLoad = BDPJettyServerHelper.jacksonJson().writeValueAsString(postEntity);
            } else {
                requestPayLoad = BDPJettyServerHelper.jacksonJson().writeValueAsString(getRequestPayloads());
            }
        }catch (JsonProcessingException e) {
            LOG.error("Failed to covert entity/request payload to a string", e);
        }
        return requestPayLoad;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public T getPostEntity() {
        return postEntity;
    }

    public void setPostEntity(T postEntity) {
        this.postEntity = postEntity;
    }

}
