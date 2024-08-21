package com.webank.wedatasphere.exchangis.datasource.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteDataSourceSuccessResult extends RemoteResult {
    private DeleteIdDTO data;

    public DeleteIdDTO getData() {
        return data;
    }

    public void setData(DeleteIdDTO data) {
        this.data = data;
    }

    public static class DeleteIdDTO {
        @JsonProperty(value = "remove_id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
