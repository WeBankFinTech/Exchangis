package com.webank.wedatasphere.exchangis.datasource.dto;

public class GetDataSourceConnectParamsResultDTO extends ResultDTO {
    private ConnectParamsDTO data;

    public ConnectParamsDTO getData() {
        return data;
    }

    public void setData(ConnectParamsDTO data) {
        this.data = data;
    }

    public static class ConnectParamsDTO {
    }
}
