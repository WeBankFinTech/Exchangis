package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Launcher Job
 */
public class ExchangisLauncherJob extends ExchangisJobBase {

    private String launchName;

    private Map<String, Object> jobContent = new HashMap<>();

    private Map<String, Object> runtimeMap = new HashMap<>();

    private String engine;

    private String proxyUser;

    private String executeNode;

    private String createUser;

    public String getLaunchName() {
        return launchName;
    }

    public void setLaunchName(String launchName) {
        this.launchName = launchName;
    }

    public Map<String, Object> getJobContent() {
        return jobContent;
    }

    public void setJobContent(Map<String, Object> jobContent) {
        this.jobContent = jobContent;
    }

    public Map<String, Object> getRuntimeMap() {
        return runtimeMap;
    }

    public void setRuntimeMap(Map<String, Object> runtimeMap) {
        this.runtimeMap = runtimeMap;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
