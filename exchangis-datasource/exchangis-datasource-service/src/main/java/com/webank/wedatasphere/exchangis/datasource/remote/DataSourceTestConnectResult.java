package com.webank.wedatasphere.exchangis.datasource.remote;

public class DataSourceTestConnectResult extends RemoteResult {

    private TestConnectData data;

    public TestConnectData getData() {
        return data;
    }

    public void setData(TestConnectData data) {
        this.data = data;
    }

    public static class TestConnectData {
    }

}
