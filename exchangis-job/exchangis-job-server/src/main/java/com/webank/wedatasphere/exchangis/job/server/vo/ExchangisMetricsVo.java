package com.webank.wedatasphere.exchangis.job.server.vo;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/2/7 19:33
 */
public class ExchangisMetricsVo {
    private Map<String, resourceUsed> resourceUsed;

    private Map<String, traffic> traffic;

    private Map<String, indicator> indicator;

    public ExchangisMetricsVo(){

    }

    public ExchangisMetricsVo(Map<String, resourceUsed> resourceUsed, Map<String, traffic> traffic, Map<String, indicator> indicator) {
        this.resourceUsed = resourceUsed;
        this.traffic = traffic;
        this.indicator = indicator;
    }

    public Map<String, ExchangisMetricsVo.resourceUsed> getResourceUsed() {
        return resourceUsed;
    }

    public void setResourceUsed(Map<String, ExchangisMetricsVo.resourceUsed> resourceUsed) {
        this.resourceUsed = resourceUsed;
    }

    public Map<String, ExchangisMetricsVo.traffic> getTraffic() {
        return traffic;
    }

    public void setTraffic(Map<String, ExchangisMetricsVo.traffic> traffic) {
        this.traffic = traffic;
    }

    public Map<String, ExchangisMetricsVo.indicator> getIndicator() {
        return indicator;
    }

    public void setIndicator(Map<String, ExchangisMetricsVo.indicator> indicator) {
        this.indicator = indicator;
    }

    public static class resourceUsed {
        private String cpu;

        private String memory;

        public resourceUsed(String cpu, String memory){
            this.cpu = cpu;
            this.memory = memory;
        }

        public String getCpu() {
            return cpu;
        }

        public void setCpu(String cpu) {
            this.cpu = cpu;
        }

        public String getMemory() {
            return memory;
        }

        public void setMemory(String memory) {
            this.memory = memory;
        }
    }

    public static class traffic{
        private String source;

        private String sink;

        private double flow;

        public traffic(String source, String sink, double flow){
            this.source = source;
            this.sink = sink;
            this.flow = flow;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSink() {
            return sink;
        }

        public void setSink(String sink) {
            this.sink = sink;
        }

        public double getFlow() {
            return flow;
        }

        public void setFlow(double flow) {
            this.flow = flow;
        }
    }

    public static class indicator{
        private Long exchangedRecords;

        private Long errorRecords;

        private Long ignoredRecords;

        public indicator(Long exchangedRecords, Long errorRecords, Long ignoredRecords){
            this.exchangedRecords = exchangedRecords;
            this.errorRecords = errorRecords;
            this.ignoredRecords = ignoredRecords;
        }

        public Long getExchangedRecords() {
            return exchangedRecords;
        }

        public void setExchangedRecords(Long exchangedRecords) {
            this.exchangedRecords = exchangedRecords;
        }

        public Long getErrorRecords() {
            return errorRecords;
        }

        public void setErrorRecords(Long errorRecords) {
            this.errorRecords = errorRecords;
        }

        public Long getIgnoredRecords() {
            return ignoredRecords;
        }

        public void setIgnoredRecords(Long ignoredRecords) {
            this.ignoredRecords = ignoredRecords;
        }
    }
}
