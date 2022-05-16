package com.webank.wedatasphere.exchangis.datasource.dto;

public class UpdateParamsVersionResultDTO extends ResultDTO {
    private VersionDTO data;

    public VersionDTO getData() {
        return data;
    }

    public void setData(VersionDTO data) {
        this.data = data;
    }

    public static class VersionDTO {
        private Long version;

        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }
}
