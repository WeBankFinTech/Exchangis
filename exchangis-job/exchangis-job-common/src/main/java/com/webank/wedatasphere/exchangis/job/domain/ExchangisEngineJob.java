package com.webank.wedatasphere.exchangis.job.domain;


import com.webank.wedatasphere.exchangis.job.utils.MemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * EngineJob
 */
public class ExchangisEngineJob extends GenericExchangisJob {


    /**
     * Job content
     */
    private Map<String, Object> jobContent = new HashMap<>();

    /**
     * Job runtime params(defined by user)
     */
    private Map<String, Object> runtimeParams = new HashMap<>();

    /**
     * Memory used in engine job
     */
    private Long memoryUsed;

    private String memoryUnit = MemUtils.StoreUnit.MB.name();

    /**
     * Cpu used in engine job
     */
    private Long cpuUsed;

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

    public Long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public Long getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(Long cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public String getMemoryUnit() {
        return memoryUnit;
    }

    public void setMemoryUnit(String memoryUnit) {
        this.memoryUnit = memoryUnit;
    }
}
