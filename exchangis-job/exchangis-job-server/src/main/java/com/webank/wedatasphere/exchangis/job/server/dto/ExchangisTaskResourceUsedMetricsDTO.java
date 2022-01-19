package com.webank.wedatasphere.exchangis.job.server.dto;

/**
 * @author tikazhang
 * @Date 2022/1/8 19:43
 */
public class ExchangisTaskResourceUsedMetricsDTO {

    private double cpu;

    private Long memory;

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }
}
