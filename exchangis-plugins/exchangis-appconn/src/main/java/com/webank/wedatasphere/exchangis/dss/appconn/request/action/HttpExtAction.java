package com.webank.wedatasphere.exchangis.dss.appconn.request.action;

import org.apache.linkis.httpclient.request.HttpAction;

/**
 * Extension http action
 */
public interface HttpExtAction extends HttpAction {

    /**
     * Set url
     * @param url url path
     */
    void setUrl(String url);
}
