package com.webank.wedatasphere.exchangis.datasource.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateDataSourceSuccessResult extends RemoteResult {
    private UpdateIdDTO data;


    public UpdateIdDTO getData() {
        return data;
    }

    public void setData(UpdateIdDTO data) {
        this.data = data;
    }

    public static class UpdateIdDTO {
        @JsonProperty(value = "update_id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
