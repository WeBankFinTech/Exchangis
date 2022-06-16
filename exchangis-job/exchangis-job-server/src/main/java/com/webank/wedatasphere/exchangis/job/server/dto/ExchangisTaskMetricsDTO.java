package com.webank.wedatasphere.exchangis.job.server.dto;

/**
 *
 * @Date 2022/1/8 19:33
 */
public class ExchangisTaskMetricsDTO {

    private ExchangisTaskResourceUsedMetricsDTO resourceUsed;

    private ExchangisTaskTrafficMetricsDTO traffic;

    private ExchangisTaskIndicatorMetricsDTO indicator;

    public ExchangisTaskResourceUsedMetricsDTO getResourceUsed() {
        return resourceUsed;
    }

    public void setResourceUsed(ExchangisTaskResourceUsedMetricsDTO resourceUsed) {
        this.resourceUsed = resourceUsed;
    }

    public ExchangisTaskTrafficMetricsDTO getTraffic() {
        return traffic;
    }

    public void setTraffic(ExchangisTaskTrafficMetricsDTO traffic) {
        this.traffic = traffic;
    }

    public ExchangisTaskIndicatorMetricsDTO getIndicator() {
        return indicator;
    }

    public void setIndicator(ExchangisTaskIndicatorMetricsDTO indicator) {
        this.indicator = indicator;
    }

    /*public static class ExchangisTaskResourceUsedMetricsDTO{

    }*/
}
