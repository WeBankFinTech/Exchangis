package com.webank.wedatasphere.exchangis.job.server.vo;

import java.util.List;

public class ExchangisTaskSpeedLimitVO {

    private List<Settings> settings;

    public List<Settings> getSettings() {
        return settings;
    }

    public void setSettings(List<Settings> settings) {
        this.settings = settings;
    }

    public static class Settings {
        private String config_key;
        private String config_name;
        private String config_value;
        private Integer sort;

        public String getConfig_key() {
            return config_key;
        }

        public void setConfig_key(String config_key) {
            this.config_key = config_key;
        }

        public String getConfig_name() {
            return config_name;
        }

        public void setConfig_name(String config_name) {
            this.config_name = config_name;
        }

        public String getConfig_value() {
            return config_value;
        }

        public void setConfig_value(String config_value) {
            this.config_value = config_value;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

}
