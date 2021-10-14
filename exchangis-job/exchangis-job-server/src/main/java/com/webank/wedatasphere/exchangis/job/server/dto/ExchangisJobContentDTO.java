package com.webank.wedatasphere.exchangis.job.server.dto;

import java.util.Map;

public class ExchangisJobContentDTO {

    private String content;

    private String proxyUser;

    private String executeNode;

    private String syncType;

    private Map<String, String> jobParams;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getExecuteNode() {
        return executeNode;
    }

    public void setExecuteNode(String executeNode) {
        this.executeNode = executeNode;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public Map<String, String> getJobParams() {
        return jobParams;
    }

    public void setJobParams(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

}
