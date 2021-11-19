package com.webank.wedatasphere.exchangis.job.domain;


import java.util.HashMap;
import java.util.Map;

/**
 * EngineJob
 */
public class ExchangisEngineJob extends ExchangisJobBase {


    private String engine;
    /**
     * Job content
     */
    private Map<String, Object> jobContent = new HashMap<>();

    /**
     * Job runtime params(defined by user)
     */
    private Map<String, Object> runtimeParams = new HashMap<>();

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Map<String, Object> getJobContent() {
        return jobContent;
    }

    public void setJobContent(Map<String, Object> jobContent) {
        this.jobContent = jobContent;
    }

    public Map<String, Object> getRuntimeParams() {
        return runtimeParams;
    }

    public void setRuntimeParams(Map<String, Object> runtimeParams) {
        this.runtimeParams = runtimeParams;
    }
}
