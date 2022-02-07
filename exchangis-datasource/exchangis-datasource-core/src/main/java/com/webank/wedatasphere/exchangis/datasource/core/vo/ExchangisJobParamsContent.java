package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExchangisJobParamsContent {

    private List<ExchangisJobParamsItem> sources;
    private List<ExchangisJobParamsItem> sinks;

    public List<ExchangisJobParamsItem> getSources() {
        return sources;
    }

    public void setSources(List<ExchangisJobParamsItem> sources) {
        this.sources = sources;
    }

    public List<ExchangisJobParamsItem> getSinks() {
        return sinks;
    }

    public void setSinks(List<ExchangisJobParamsItem> sinks) {
        this.sinks = sinks;
    }

    public static class ExchangisJobParamsItem {
        @JsonProperty("config_key")
        private String configKey;

        @JsonProperty("config_name")
        private String configName;

        @JsonProperty("config_value")
        private Object configValue;

        private Integer sort;

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigName() {
            return configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }

        public Object getConfigValue() {
            return configValue;
        }

        public void setConfigValue(Object configValue) {
            this.configValue = configValue;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

}
