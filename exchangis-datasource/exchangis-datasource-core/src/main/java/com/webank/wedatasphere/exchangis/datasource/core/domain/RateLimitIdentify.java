package com.webank.wedatasphere.exchangis.datasource.core.domain;

import java.util.*;

public class RateLimitIdentify {

    public enum LimitRealm {

        MODEL("MODEL");

        private String type;

        LimitRealm(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    public enum RateLimitType {

        NONE("", "", "", 0),

        FLOW_RATE_LIMIT("flow_rate_limit", "流量(flow_rate)", "MB", 1),

        RECORD_RATE_LIMIT("record_rate_limit", "记录速率(record_rate)", "record", 10000),

        PARALLEL_LIMIT("parallel_limit", "并行度(parallel)", "unit", 100);

        public static Map<String, RateLimitType> TYPES_HOLDER = new HashMap<>();

        static {
            TYPES_HOLDER.put(PARALLEL_LIMIT.getKey(), PARALLEL_LIMIT);
            TYPES_HOLDER.put(FLOW_RATE_LIMIT.getKey(), FLOW_RATE_LIMIT);
            TYPES_HOLDER.put(RECORD_RATE_LIMIT.getKey(), RECORD_RATE_LIMIT);
            TYPES_HOLDER.put(PARALLEL_LIMIT.getKey(), PARALLEL_LIMIT);
        }

        private String key;

        private String limitItem;

        private String unit;

        private Integer defaultValue;

        public String getKey() {
            return key;
        }

        public String getUnit() {
            return unit;
        }

        public Integer getDefaultValue() {
            return defaultValue;
        }

        public String getLimitItem() {
            return limitItem;
        }

        RateLimitType(String key, String limitItem, String unit, Integer defaultValue) {
            this.key = key;
            this.limitItem = limitItem;
            this.unit = unit;
            this.defaultValue = defaultValue;
        }

        public static List<RateLimitType> getRateLimitTypes() {
            return Arrays.asList(FLOW_RATE_LIMIT, RECORD_RATE_LIMIT, PARALLEL_LIMIT);
        }

        public static RateLimitType valueOfType(String typeKey){
            return TYPES_HOLDER.getOrDefault(typeKey, NONE);
        }
    }

}
