package com.webank.wedatasphere.exchangis.datasource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteDataSourceSuccessResultDTO extends ResultDTO {
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
