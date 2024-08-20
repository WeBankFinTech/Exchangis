package com.webank.wedatasphere.exchangis.datasource.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateDataSourceSuccessResult extends RemoteResult {
    private InsertIdVo data;


    public InsertIdVo getData() {
        return data;
    }

    public void setData(InsertIdVo data) {
        this.data = data;
    }

    public static class InsertIdVo {
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
