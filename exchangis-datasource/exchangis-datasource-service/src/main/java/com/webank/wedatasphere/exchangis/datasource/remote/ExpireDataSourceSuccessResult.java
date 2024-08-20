package com.webank.wedatasphere.exchangis.datasource.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpireDataSourceSuccessResult extends RemoteResult {
    private ExpireIdDTO data;


    public ExpireIdDTO getData() {
        return data;
    }

    public void setData(ExpireIdDTO data) {
        this.data = data;
    }

    public static class ExpireIdDTO {
        @JsonProperty(value = "expire_id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
