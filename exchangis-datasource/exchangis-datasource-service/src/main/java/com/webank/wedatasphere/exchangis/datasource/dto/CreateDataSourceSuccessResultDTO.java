package com.webank.wedatasphere.exchangis.datasource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateDataSourceSuccessResultDTO extends ResultDTO {
    private InsertIdDTO data;


    public InsertIdDTO getData() {
        return data;
    }

    public void setData(InsertIdDTO data) {
        this.data = data;
    }

    public static class InsertIdDTO {
        @JsonProperty(value = "insert_id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
