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
        @JsonProperty(value = "insertId")
        private Long insertId;

        public Long getId() {
            return insertId;
        }

        public void setId(Long insertId) {
            this.insertId = insertId;
        }
    }
}
