package com.webank.wedatasphere.exchangis.job.domain;


import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;

import java.util.*;

/**
 * EngineJob
 */
public class ExchangisEngineJob extends GenericExchangisJob {

    public ExchangisEngineJob(){

    }

    public ExchangisEngineJob(ExchangisEngineJob engineJob){
        if (Objects.nonNull(engineJob)) {
            setName(engineJob.getName());
            setEngineType(engineJob.getEngineType());
            getJobContent().putAll(engineJob.getJobContent());
            getRuntimeParams().putAll(engineJob.getRuntimeParams());
            setMemoryUsed(engineJob.getMemoryUsed());
            getResources().addAll(engineJob.getResources());
        }
    }
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
     * If lock the unit of memory
     */
    private boolean memoryUnitLock = false;
    /**
     * Cpu used in engine job
     */
    private Long cpuUsed;

    /**
     * Engine resources
     */
    private List<EngineResource> resources = new ArrayList<>();

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

    public List<EngineResource> getResources() {
        return resources;
    }

    public void setResources(List<EngineResource> resources) {
        this.resources = resources;
    }

    public boolean isMemoryUnitLock() {
        return memoryUnitLock;
    }

    public void setMemoryUnitLock(boolean memoryUnitLock) {
        this.memoryUnitLock = memoryUnitLock;
    }
}
