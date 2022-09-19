package com.webank.wedatasphere.exchangis.datasource.core.domain;

public enum Classifier {

    ELASTICSEARCH("分布式全文索引"),

    HIVE("大数据存储"),

    MONGODB("非关系型数据库"),

    MYSQL("关系型数据库"),

    SFTP("sftp连接"),

    ORACLE("关系型数据库");

    public String name;

    Classifier(String name) {
        this.name = name;
    }
}
