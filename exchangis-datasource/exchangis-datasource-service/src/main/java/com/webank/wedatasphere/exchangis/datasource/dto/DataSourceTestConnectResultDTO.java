package com.webank.wedatasphere.exchangis.datasource.dto;

public class DataSourceTestConnectResultDTO extends ResultDTO {

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
