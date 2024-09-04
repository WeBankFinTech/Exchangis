package com.webank.wedatasphere.exchangis.datasource.utils;

import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitIdentify;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitUsed;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.content.JobSettingsDefine;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class RateLimitTool {

    /**
     * Print rate limit log
     *
     * @param applyUsed  apply used
     * @param sourceUsed used in source destination
     * @param sinkUsed   used in sink destination
     * @return log content
     */
    public static String getLimitLog(List<RateLimitUsed> applyUsed,
                                     List<RateLimitUsed> sourceUsed, List<RateLimitUsed> sinkUsed) {
        return new StringBuilder().append("任务被限速(Job has been rate limited):  ")
                .append(getLimitItem(applyUsed, sinkUsed))
                .append("\n")
                .append("请求速率(Request rate):  [")
                .append(getLimitUsedLog(applyUsed, false))
                .append("]\n")
                .append("数据源限速已使用情况(Output rate limit):  [")
                .append(getLimitUsedLog(sinkUsed, true))
                .append("]\n")
                .toString();
    }

    public static List<RateLimitUsed> generateRateLimitUsed(RateLimit rateLimit) {
        List<RateLimitUsed> rateLimitUsedList = new ArrayList<>();
        RateLimitIdentify.RateLimitType.getRateLimitTypes().forEach(type -> {
            RateLimitUsed limitUsed = new RateLimitUsed();
            String key = type.getKey();
            limitUsed.setRateLimitId(rateLimit.getId());
            limitUsed.setRateLimitType(key);
            switch (type) {
                case FLOW_RATE_LIMIT:

                    limitUsed.setRateLimitUsed(0l);
                    limitUsed.setRateLimitTotal(rateLimit.getFlowRateLimit());
                    break;
                case RECORD_RATE_LIMIT:
                    limitUsed.setRateLimitUsed(0l);
                    limitUsed.setRateLimitTotal(rateLimit.getRecordRateLimit());
                    break;
                case PARALLEL_LIMIT:
                    limitUsed.setRateLimitUsed(0l);
                    limitUsed.setRateLimitTotal(rateLimit.getParallelLimit());
                    break;
                default:
                    break;
            }
            limitUsed.setCreateUser(rateLimit.getCreateUser());
            limitUsed.setModifyUser(rateLimit.getModifyUser());
            rateLimitUsedList.add(limitUsed);
        });
        return rateLimitUsedList;
    }

    public static List<RateLimitUsed> generateRateLimitUsed(RateLimit rateLimit, Map<String, Object> rateParams) {
        if (Objects.isNull(rateParams) || rateParams.isEmpty()) {
            return null;
        }
        List<RateLimitUsed> result = new ArrayList<>();
        for (String key : rateParams.keySet()) {
            RateLimitUsed limitUsed = new RateLimitUsed();
            if (StringUtils.equals(JobSettingsDefine.SETTING_SPEED_BYTE, key)) {
                Long speedBytes = Long.valueOf(String.valueOf(rateParams.get(key)));
                if (StringUtils.equals("MB", rateLimit.getFlowRateLimitUnit())) {
                    speedBytes = speedBytes/1024/1024;
                }
                limitUsed.setRateLimitUsed(speedBytes);
                limitUsed.setRateLimitTotal(rateLimit.getFlowRateLimit());
                limitUsed.setRateLimitType(RateLimitIdentify.RateLimitType.FLOW_RATE_LIMIT.getKey());
            }
            if (StringUtils.equals(JobSettingsDefine.SETTING_SPEED_RECORD, key)) {
                limitUsed.setRateLimitUsed(Long.valueOf(String.valueOf(rateParams.get(key))));
                limitUsed.setRateLimitTotal(rateLimit.getRecordRateLimit());
                limitUsed.setRateLimitType(RateLimitIdentify.RateLimitType.RECORD_RATE_LIMIT.getKey());
            }
            if (StringUtils.equals(JobSettingsDefine.SETTING_SPEED_CHANNEL, key)) {
                limitUsed.setRateLimitUsed(Long.valueOf(String.valueOf(rateParams.get(key))));
                limitUsed.setRateLimitTotal(rateLimit.getParallelLimit());
                limitUsed.setRateLimitType(RateLimitIdentify.RateLimitType.PARALLEL_LIMIT.getKey());
            }
            limitUsed.setModifyUser(rateLimit.getModifyUser());
            limitUsed.setRateLimitId(rateLimit.getId());
            result.add(limitUsed);
        }
        return result;
    }

    /**
     * Generate rate limit used list
     * Add/Update: rateLimitUsed is 0, rateLimitTotal same with rateLimit
     * Apply/Release: rateLimitUsed same with job, rateLimitTotal same with rateLimit
     *
     * @param rateLimit rate limit rule
     * @param jobInfo   job info
     * @return rate limit used list
     */
    public static List<RateLimitUsed> generateRateLimitUsed(RateLimit rateLimit, ExchangisJobInfo jobInfo) {
        List<RateLimitUsed> rateLimitUsedList = new ArrayList<>();
        boolean jobNotEmpty = Objects.nonNull(jobInfo) && Objects.nonNull(jobInfo.getJobContent());
        RateLimitIdentify.RateLimitType.getRateLimitTypes().forEach(type -> {
            AtomicReference<Long> speedByteUsed = new AtomicReference<>(0l);
            AtomicReference<Long> speedRecordUsed = new AtomicReference<>(0l);
            AtomicReference<Long> speedAdvanceUsed = new AtomicReference<>(0l);
            if (jobNotEmpty) {
                List<ExchangisJobInfoContent> jobInfoContents = JobUtils.parseJobContent(jobInfo.getJobContent());
                jobInfoContents.stream().flatMap(content -> content.getSettings().stream())
                        .forEach(item -> {
                            String configName = item.getConfigKey();
                            switch (configName) {
                                case JobSettingsDefine.SETTING_SPEED_BYTE :
                                    speedByteUsed.updateAndGet(v -> v + (Integer) item.getConfigValue());
                                case JobSettingsDefine.SETTING_SPEED_RECORD:
                                    speedRecordUsed.updateAndGet(v -> v + (Integer) item.getConfigValue());
                                case JobSettingsDefine.SETTING_SPEED_CHANNEL:
                                    speedAdvanceUsed.updateAndGet(v -> v + (Integer) item.getConfigValue());
                            }
                        });
            }
            RateLimitUsed limitUsed = new RateLimitUsed();
            String key = type.getKey();
            switch (type) {
                case FLOW_RATE_LIMIT:
                    limitUsed.setRateLimitUsed(speedByteUsed.get());
                    limitUsed.setRateLimitTotal(rateLimit.getFlowRateLimit());
                    break;
                case RECORD_RATE_LIMIT:
                    limitUsed.setRateLimitUsed(speedRecordUsed.get());
                    limitUsed.setRateLimitTotal(rateLimit.getRecordRateLimit());
                    break;
                case PARALLEL_LIMIT:
                    limitUsed.setRateLimitUsed(speedAdvanceUsed.get());
                    limitUsed.setRateLimitTotal(rateLimit.getParallelLimit());
                    break;
                default:
                    break;
            }
            if (Objects.isNull(jobInfo) || limitUsed.getRateLimitUsed() > 0) {
                limitUsed.setRateLimitType(key);
                limitUsed.setModifyUser(rateLimit.getModifyUser());
                limitUsed.setRateLimitId(rateLimit.getId());
                rateLimitUsedList.add(limitUsed);
            }
        });
        return rateLimitUsedList;
    }

    /**
     * Get limit item
     *
     * @param applyUsed
     * @param sinkUsed
     * @return
     */
    private static String getLimitItem(List<RateLimitUsed> applyUsed, List<RateLimitUsed> sinkUsed) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < applyUsed.size(); i++) {
            RateLimitUsed apply = applyUsed.get(i);
            RateLimitIdentify.RateLimitType type = RateLimitIdentify.RateLimitType.valueOfType(apply.getRateLimitType());
            RateLimitUsed used = sinkUsed.get(i);
            if (type != RateLimitIdentify.RateLimitType.NONE &&
                    apply.getRateLimitUsed() > (used.getRateLimitTotal() - used.getRateLimitUsed())) {
                builder.append(type.getLimitItem()).append("超过限速阈值");
                if (i < applyUsed.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }

    /**
     * Get log for limit used
     *
     * @param limitUsedList limit used
     * @param includeTotal  include total limit
     */
    private static String getLimitUsedLog(List<RateLimitUsed> limitUsedList, boolean includeTotal) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limitUsedList.size(); i++) {
            RateLimitUsed limitUsed = limitUsedList.get(i);
            RateLimitIdentify.RateLimitType type = RateLimitIdentify.RateLimitType.valueOfType(limitUsed.getRateLimitType());
            if (type != RateLimitIdentify.RateLimitType.NONE) {
                builder.append(type.getLimitItem()).append(": ").append(limitUsed.getRateLimitUsed());
                if (includeTotal) {
                    builder.append("/").append(limitUsed.getRateLimitTotal());
                }
                builder.append(" ").append(type.getUnit());
                if (i < limitUsedList.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }
}
