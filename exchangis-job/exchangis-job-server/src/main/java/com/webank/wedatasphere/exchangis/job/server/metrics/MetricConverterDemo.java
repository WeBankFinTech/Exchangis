/*
package com.webank.wedatasphere.exchangis.job.server.metrics;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.MetricConverterFactory;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.MetricsConverter;

import java.util.HashMap;
import java.util.Map;

*/
/**
 *
 * @Date 2022/1/10 20:56
 *//*

public class MetricConverterDemo {
    private static MetricConverterFactory factory = new MetricConverterFactory() {
        @Override
        public <T extends MetricsVo> MetricsConverter<T> getOrCreateMetricsConverter(Class<T> metricsVoClass, String engineType) {
            return null;
        }
    };

    public static void main(String[] args){

        Map<String, Object> metricsMap = new HashMap<>();
        LaunchedTaskMetricVo launchedTaskMetricVo = factory.getOrCreateMetricsConverter(LaunchedTaskMetricVo.class, "sqoop").convert(metricsMap);

    }

    public static class LaunchedExchangisTaskVo {
        public LaunchedExchangisTaskVo(LaunchedExchangisTaskEntity taskEntity){

        }
    }

    public static class LaunchedExchangisTaskProgressVo extends LaunchedExchangisTaskVo {

        public LaunchedExchangisTaskProgressVo(LaunchedExchangisTaskEntity taskEntity) {
            super(taskEntity);
        }
    }

    public static class LaunchedExchangisTaskMetricsVo extends LaunchedExchangisTaskVo {
        private LaunchedTaskMetricVo metrics;
        private LaunchedTaskRescouceMetricVo resourceMetrics;
        private LaunchedTaskTrafficeMetricVo trafficMetrics;
        private LaunchedTaskIndicatorMetricVo IndicatorMetrics;

        public LaunchedExchangisTaskMetricsVo(LaunchedExchangisTaskEntity taskEntity) {
            super(taskEntity);
            this.metrics = factory.getOrCreateMetricsConverter(LaunchedTaskMetricVo.class, taskEntity.getEngineType()).convert(taskEntity.getMetricsMap());
            this.resourceMetrics = factory.getOrCreateMetricsConverter(LaunchedresourcTaskMetricVo.class, taskEntity.getEngineType(), taskEntity.getMetricsMap());
            this.trafficMetrics = factory.getOrCreateMetricsConverter(LaunchedtrafficTaskMetricVo.class, taskEntity.getEngineType(), taskEntity.getMetricsMap());
            this.IndicatorMetrics = factory.getOrCreateMetricsConverter(LaunchedIndicatorTaskMetricVo.class, taskEntity.getEngineType(), taskEntity.getMetricsMap());

        }
    }

    public static class LaunchedTaskMetricVo implements MetricsVo{


    }
}
*/
