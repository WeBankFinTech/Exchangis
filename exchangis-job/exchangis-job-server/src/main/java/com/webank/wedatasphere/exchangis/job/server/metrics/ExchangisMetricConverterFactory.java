package com.webank.wedatasphere.exchangis.job.server.metrics;

import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.DataxMetricConverter;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.MetricsConverter;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.RegisterMetricConverterFactory;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.SqoopMetricConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default metric converters factory
 */
@Component
public class ExchangisMetricConverterFactory implements RegisterMetricConverterFactory<ExchangisMetricsVo> {

    private Map<String, MetricsConverter<ExchangisMetricsVo>> registers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(EngineTypeEnum.SQOOP.name(), new SqoopMetricConverter());
        register(EngineTypeEnum.DATAX.name(), new DataxMetricConverter());
    }
    @Override
    public void register(String engineType, MetricsConverter<ExchangisMetricsVo> converter) {
        if (StringUtils.isNotBlank(engineType)) {
            registers.put(engineType.toLowerCase(), converter);
        }
    }

    @Override
    public MetricsConverter<ExchangisMetricsVo> getOrCreateMetricsConverter(String engineType) {
        return registers.getOrDefault(StringUtils.isNotBlank(engineType)? engineType.toLowerCase() : "null", metricMap -> {
            // Return the empty vo
            return new ExchangisMetricsVo();
        });
    }
}
