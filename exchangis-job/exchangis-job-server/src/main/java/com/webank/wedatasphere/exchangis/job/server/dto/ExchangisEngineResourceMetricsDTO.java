package com.webank.wedatasphere.exchangis.job.server.dto;

public class ExchangisEngineResourceMetricsDTO {

    private String engine;

    private String cpu;

    private String mem;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMem() {
        return mem;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }
}
