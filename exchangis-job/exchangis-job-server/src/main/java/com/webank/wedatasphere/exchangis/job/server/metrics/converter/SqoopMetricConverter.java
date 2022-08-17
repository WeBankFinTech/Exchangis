package com.webank.wedatasphere.exchangis.job.server.metrics.converter;

import com.webank.wedatasphere.exchangis.job.server.metrics.ExchangisMetricsVo;
import com.webank.wedatasphere.exchangis.job.server.utils.JsonEntity;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Convert the metrics in sqoop engine
 */
public class SqoopMetricConverter extends AbstractMetricConverter implements AbstractMetricConverter.MetricsParser {

    @Override
    public String resourceUsedKey() {
        return "";
    }

    @Override
    public String trafficKey() {
        return "";
    }

    @Override
    public String indicatorKey() {
        return JsonEntity.encodePath("org.apache.hadoop.mapreduce.TaskCounter");
    }

    @Override
    protected AbstractMetricConverter.MetricsParser getParser() {
        return this;
    }

    @Override
    public ExchangisMetricsVo.ResourceUsed parseResourceUsed(String key, JsonEntity rawValue) {
        ExchangisMetricsVo.ResourceUsed resourceUsed = new ExchangisMetricsVo.ResourceUsed();
        String nodeResource = rawValue.getString("NodeResourceJson");
        if (StringUtils.isNotBlank(nodeResource)){
            JsonEntity nodeResourceJson = JsonEntity.from(nodeResource);
            String memoryUnit = nodeResourceJson.getString("driver.memory");
            if (StringUtils.isNotBlank(memoryUnit)){
                String[] memory = memoryUnit.split(" ");
                resourceUsed.setMemory(memory.length >= 2 ?
                        MemUtils.convertToMB((long) Double.parseDouble(memory[0]), memory[1]) : (long) Double.parseDouble(memory[0]));
            }
            String cpuVCores = nodeResourceJson.getString("driver.cpu");
            if (StringUtils.isNotBlank(cpuVCores)){
                resourceUsed.setCpu(Integer.parseInt(cpuVCores));
            }
        }
        return resourceUsed;
    }

    @Override
    public ExchangisMetricsVo.Traffic parseTraffic(String key, JsonEntity rawValue) {
        ExchangisMetricsVo.Traffic traffic = new ExchangisMetricsVo.Traffic();
        Double records = rawValue.getDouble(JsonEntity.encodePath("org.apache.hadoop.mapreduce.TaskCounter") + ".MAP_OUTPUT_RECORDS");
        Double runTime = rawValue.getDouble("MetricsRunTime");
        if (Objects.nonNull(records) && Objects.nonNull(runTime)){
            traffic.setFlow(new BigDecimal(records / runTime * 1000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        return traffic;
    }

    @Override
    public ExchangisMetricsVo.Indicator parseIndicator(String key, JsonEntity rawValue) {
        ExchangisMetricsVo.Indicator indicator = new ExchangisMetricsVo.Indicator();
        Long records = rawValue.getLong("MAP_OUTPUT_RECORDS");
        if (Objects.nonNull(records)){
            indicator.setExchangedRecords(records);
        }
        return indicator;
    }

}
