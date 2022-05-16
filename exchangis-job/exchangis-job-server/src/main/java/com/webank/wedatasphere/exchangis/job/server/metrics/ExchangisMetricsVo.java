package com.webank.wedatasphere.exchangis.job.server.metrics;


/**
 */
public class ExchangisMetricsVo implements MetricsVo{

    /**
     * Resource used
     */
    private ResourceUsed resourceUsed;

    /**
     * Traffic
     */
    private Traffic traffic;

    /**
     * Indicator
     */
    private Indicator indicator;

    public ExchangisMetricsVo(){

    }

    public ExchangisMetricsVo(ResourceUsed resourceUsed, Traffic traffic, Indicator indicator) {
        this.resourceUsed = resourceUsed;
        this.traffic = traffic;
        this.indicator = indicator;
    }

    public ResourceUsed getResourceUsed() {
        return resourceUsed;
    }

    public void setResourceUsed(ResourceUsed resourceUsed) {
        this.resourceUsed = resourceUsed;
    }

    public Traffic getTraffic() {
        return traffic;
    }

    public void setTraffic(Traffic traffic) {
        this.traffic = traffic;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public static class ResourceUsed {
        private double cpu = 0.0;

        private long memory = 0;

        public ResourceUsed(double cpu, long memory){
            this.cpu = cpu;
            this.memory = memory;
        }

        public ResourceUsed(){

        }

        public double getCpu() {
            return cpu;
        }

        public void setCpu(double cpu) {
            this.cpu = cpu;
        }

        public long getMemory() {
            return memory;
        }

        public void setMemory(long memory) {
            this.memory = memory;
        }
    }

    public static class Traffic{
        private String source = "source";

        private String sink = "sink";

        private double flow;

        public Traffic(String source, String sink, double flow){
            this.source = source;
            this.sink = sink;
            this.flow = flow;
        }

        public Traffic(){

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

    public static class Indicator{
        private long exchangedRecords = 0;

        private long errorRecords = 0;

        private long ignoredRecords = 0;

        public Indicator(long exchangedRecords, long errorRecords, long ignoredRecords){
            this.exchangedRecords = exchangedRecords;
            this.errorRecords = errorRecords;
            this.ignoredRecords = ignoredRecords;
        }

        public Indicator(){

        }
        public long getExchangedRecords() {
            return exchangedRecords;
        }

        public void setExchangedRecords(Long exchangedRecords) {
            this.exchangedRecords = exchangedRecords;
        }

        public long getErrorRecords() {
            return errorRecords;
        }

        public void setErrorRecords(Long errorRecords) {
            this.errorRecords = errorRecords;
        }

        public long getIgnoredRecords() {
            return ignoredRecords;
        }

        public void setIgnoredRecords(Long ignoredRecords) {
            this.ignoredRecords = ignoredRecords;
        }
    }
}
