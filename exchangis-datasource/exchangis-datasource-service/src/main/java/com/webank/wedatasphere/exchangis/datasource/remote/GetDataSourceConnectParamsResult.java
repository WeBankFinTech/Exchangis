package com.webank.wedatasphere.exchangis.datasource.remote;

public class GetDataSourceConnectParamsResult extends RemoteResult {
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
