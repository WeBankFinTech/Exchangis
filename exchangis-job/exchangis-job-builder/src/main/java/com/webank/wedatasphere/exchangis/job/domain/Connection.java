package com.webank.wedatasphere.exchangis.job.domain;

public class Connection {

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String[] getTable() {
        return table;
    }

    public void setTable(String[] table) {
        this.table = table;
    }

    private String jdbcUrl;

    private String[] table;
}
