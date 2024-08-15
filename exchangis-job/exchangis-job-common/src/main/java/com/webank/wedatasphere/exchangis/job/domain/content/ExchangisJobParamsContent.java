package com.webank.wedatasphere.exchangis.job.domain.content;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExchangisJobParamsContent {

    /**
     * Source params
     */
    private List<ExchangisJobParamsItem> sources;

    /**
     * Sink params
     */
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
        @JsonAlias({"key", "k"})
        private String configKey;

        @JsonProperty("config_name")
        @JsonAlias({"name", "n"})
        private String configName;

        @JsonProperty("config_value")
        @JsonAlias({"value", "v"})
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
