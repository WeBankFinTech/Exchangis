package com.webank.wedatasphere.exchangis.job.server.metrics.converter;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.metrics.ExchangisMetricsVo;
import com.webank.wedatasphere.exchangis.job.server.utils.JsonEntity;

import java.util.Map;
import java.util.Objects;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.METRICS_OP_ERROR;

/**
 * Abstract converter
 */
public abstract class AbstractMetricConverter implements MetricsConverter<ExchangisMetricsVo> {
    /**
     * Convert method
     * @param metricMap map value
     * @return
     */
    @Override
    public ExchangisMetricsVo convert(Map<String, Object> metricMap) throws ExchangisJobServerException {
        ExchangisMetricsVo metricsVo = new ExchangisMetricsVo();
        MetricsParser metricsParser = getParser();
        if (Objects.nonNull(metricsParser) &&
                Objects.nonNull(metricMap) && !metricMap.isEmpty()){
                JsonEntity metric = JsonEntity.from(metricMap);
            try {
                // ResourceUsed
                JsonEntity resourceUsedEntity = metric.getConfiguration(metricsParser.resourceUsedKey());
                ExchangisMetricsVo.ResourceUsed resourceUsed = Objects.nonNull(resourceUsedEntity) ?
                        metricsParser.parseResourceUsed(metricsParser.resourceUsedKey(), resourceUsedEntity) : null;
                metricsVo.setResourceUsed(Objects.nonNull(resourceUsed) ? resourceUsed : new ExchangisMetricsVo.ResourceUsed());
            } catch (Exception e){
                throw new ExchangisJobServerException(METRICS_OP_ERROR.getCode(), "Exception in parsing \"resourceUsed\" info", e);
            }
            try {
                // Traffic
                JsonEntity trafficEntity = metric.getConfiguration(metricsParser.trafficKey());
                ExchangisMetricsVo.Traffic traffic = Objects.nonNull(trafficEntity) ?
                        metricsParser.parseTraffic(metricsParser.trafficKey(), trafficEntity) : null;
                metricsVo.setTraffic(Objects.nonNull(traffic) ? traffic : new ExchangisMetricsVo.Traffic());
            } catch (Exception e){
                throw new ExchangisJobServerException(METRICS_OP_ERROR.getCode(), "Exception in parsing \"traffic\" info", e);
            }
            try {
                // Indicator
                JsonEntity indicatorEntity = metric.getConfiguration(metricsParser.indicatorKey());
                ExchangisMetricsVo.Indicator indicator = Objects.nonNull(indicatorEntity) ?
                        metricsParser.parseIndicator(metricsParser.indicatorKey(), indicatorEntity) : null;
                metricsVo.setIndicator(Objects.nonNull(indicator) ? indicator : new ExchangisMetricsVo.Indicator());
            }catch (Exception e){
                throw new ExchangisJobServerException(METRICS_OP_ERROR.getCode(), "Exception in parsing \"indicator\" info", e);
            }
        }
        return metricsVo;
    }

    /**
     * Get parser
     * @return parser
     */
    protected abstract MetricsParser getParser();

    protected interface MetricsParser{
        /**
         * ResourceUsed key
         * @return key
         */
        default String resourceUsedKey(){ return "-"; };

        /**
         * Traffic key
         * @return key
         */
        default String trafficKey(){ return "-"; };

        /**
         * Indicator key
         * @return key
         */
        default String indicatorKey(){ return "-"; };

        ExchangisMetricsVo.ResourceUsed parseResourceUsed(String key, JsonEntity rawValue);

        ExchangisMetricsVo.Traffic parseTraffic(String key, JsonEntity rawValue);

        ExchangisMetricsVo.Indicator parseIndicator(String key, JsonEntity rawValue);
    }

}
