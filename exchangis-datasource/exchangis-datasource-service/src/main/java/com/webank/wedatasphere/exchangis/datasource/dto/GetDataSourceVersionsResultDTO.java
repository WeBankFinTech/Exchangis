package com.webank.wedatasphere.exchangis.datasource.dto;

import java.util.List;
import java.util.Map;

public class GetDataSourceVersionsResultDTO extends ResultDTO {

    private VersionDataDTO data;

    public VersionDataDTO getData() {
        return data;
    }

    public void setData(VersionDataDTO data) {
        this.data = data;
    }

    public static class VersionDataDTO {
        private List<VersionDTO> versions;

        public List<VersionDTO> getVersions() {
            return versions;
        }

        public void setVersions(List<VersionDTO> versions) {
            this.versions = versions;
        }
    }

    public static class VersionDTO {
        private Long versionId;
        private Long dataSourceId;
        private Map<String, Object> connectParams;
        private String comment;
        private String createUser;

        public Long getVersionId() {
            return versionId;
        }

        public void setVersionId(Long versionId) {
            this.versionId = versionId;
        }

        public Long getDataSourceId() {
            return dataSourceId;
        }

        public void setDataSourceId(Long dataSourceId) {
            this.dataSourceId = dataSourceId;
        }

        public Map<String, Object> getConnectParams() {
            return connectParams;
        }

        public void setConnectParams(Map<String, Object> connectParams) {
            this.connectParams = connectParams;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }
    }

}
