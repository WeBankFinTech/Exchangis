package com.webank.wedatasphere.exchangis.datasource.dto;

import java.util.Map;

public class GetDataSourceInfoResultDTO extends ResultDTO {
    private DataSourceInfoDTO data;

    public DataSourceInfoDTO getData() {
        return data;
    }

    public void setData(DataSourceInfoDTO data) {
        this.data = data;
    }

    public static class DataSourceInfoDTO {
        private DataSourceItemDTO info;

        public DataSourceItemDTO getInfo() {
            return info;
        }

        public void setInfo(DataSourceItemDTO info) {
            this.info = info;
        }
    }

    public static class DataSourceItemDTO {
        private Long id;
        private String dataSourceName;
        private String dataSourceDesc;
        private Long dataSourceTypeId;
        private String createIdentify;
        private String createSystem;
        private Map<String, Object> connectParams;
        private Long createTime;
        private String createUser;
        private Long modifyTime;
        private String modifyUser;
        private String labels;
        private String label;
        private Long versionId;
        private Integer publishedVersionId;
        private Boolean expire;
        private DataSourceItemDsTypeDTO dataSourceType;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDataSourceName() {
            return dataSourceName;
        }

        public void setDataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
        }

        public String getDataSourceDesc() {
            return dataSourceDesc;
        }

        public void setDataSourceDesc(String dataSourceDesc) {
            this.dataSourceDesc = dataSourceDesc;
        }

        public Long getDataSourceTypeId() {
            return dataSourceTypeId;
        }

        public void setDataSourceTypeId(Long dataSourceTypeId) {
            this.dataSourceTypeId = dataSourceTypeId;
        }

        public String getCreateIdentify() {
            return createIdentify;
        }

        public void setCreateIdentify(String createIdentify) {
            this.createIdentify = createIdentify;
        }

        public String getCreateSystem() {
            return createSystem;
        }

        public void setCreateSystem(String createSystem) {
            this.createSystem = createSystem;
        }

        public Map<String, Object> getConnectParams() {
            return connectParams;
        }

        public void setConnectParams(Map<String, Object> connectParams) {
            this.connectParams = connectParams;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getLabels() {
            return labels;
        }

        public void setLabels(String labels) {
            this.labels = labels;
            this.label = labels;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getPublishedVersionId() {
            return publishedVersionId;
        }

        public void setPublishedVersionId(Integer publishedVersionId) {
            this.publishedVersionId = publishedVersionId;
        }

        public Long getVersionId() {
            return versionId;
        }

        public void setVersionId(Long versionId) {
            this.versionId = versionId;
        }

        public Boolean getExpire() {
            return expire;
        }

        public void setExpire(Boolean expire) {
            this.expire = expire;
        }

        public DataSourceItemDsTypeDTO getDataSourceType() {
            return dataSourceType;
        }

        public void setDataSourceType(DataSourceItemDsTypeDTO dataSourceType) {
            this.dataSourceType = dataSourceType;
        }

        public Long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(Long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getModifyUser() {
            return modifyUser;
        }

        public void setModifyUser(String modifyUser) {
            this.modifyUser = modifyUser;
        }
    }

    public static class DataSourceItemDsTypeDTO {
        private String name;
        private Integer layers;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getLayers() {
            return layers;
        }

        public void setLayers(Integer layers) {
            this.layers = layers;
        }
    }

}
