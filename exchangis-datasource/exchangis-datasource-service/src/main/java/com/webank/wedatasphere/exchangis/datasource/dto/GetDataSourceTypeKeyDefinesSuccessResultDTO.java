package com.webank.wedatasphere.exchangis.datasource.dto;


import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;

public class GetDataSourceTypeKeyDefinesSuccessResultDTO extends ResultDTO {
    private DataSourceKeyDefinition data;

    public DataSourceKeyDefinition getData() {
        return data;
    }

    public void setData(DataSourceKeyDefinition data) {
        this.data = data;
    }

    public static class DataSourceKeyDefinition {
        private Long id;
        private String key;
        private String description;
        private String name;
        private String defaultValue;
        private DataSourceParamKeyDefinition.ValueType valueType;
        private DataSourceParamKeyDefinition.Scope scope;
        private boolean require;
        private String valueRegex;
        private Long refId;
        private String refValue;
        private String dataSource;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public DataSourceParamKeyDefinition.ValueType getValueType() {
            return valueType;
        }

        public void setValueType(DataSourceParamKeyDefinition.ValueType valueType) {
            this.valueType = valueType;
        }

        public DataSourceParamKeyDefinition.Scope getScope() {
            return scope;
        }

        public void setScope(DataSourceParamKeyDefinition.Scope scope) {
            this.scope = scope;
        }

        public boolean isRequire() {
            return require;
        }

        public void setRequire(boolean require) {
            this.require = require;
        }

        public String getValueRegex() {
            return valueRegex;
        }

        public void setValueRegex(String valueRegex) {
            this.valueRegex = valueRegex;
        }

        public Long getRefId() {
            return refId;
        }

        public void setRefId(Long refId) {
            this.refId = refId;
        }

        public String getRefValue() {
            return refValue;
        }

        public void setRefValue(String refValue) {
            this.refValue = refValue;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }
    }
}
