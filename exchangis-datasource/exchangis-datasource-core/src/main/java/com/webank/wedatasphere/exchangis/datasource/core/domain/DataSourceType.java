package com.webank.wedatasphere.exchangis.datasource.core.domain;

public enum DataSourceType {

    ELASTICSEARCH("ELASTICSEARCH"),

    HIVE("HIVE"),

    MONGODB("MONGODB"),

    MYSQL("MYSQL"),

    SFTP("SFTP");

    public String name;

    DataSourceType(String name) {
        this.name = name;
    }
}
