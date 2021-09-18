package com.webank.wedatasphere.exchangis.datasource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpireDataSourceSuccessResultDTO extends ResultDTO {
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
