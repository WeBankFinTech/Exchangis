package com.webank.wedatasphere.exchangis.datasource.core.domain;

public enum ExchangisDataSourceType {

    ELASTICSEARCH("ELASTICSEARCH", "分布式全文索引"),

    HIVE("HIVE", "大数据存储"),

    MONGODB("MONGODB", "非关系型数据库"),

    MYSQL("MYSQL", "关系型数据库"),

    SFTP("SFTP", "sftp连接"),

    ORACLE("ORACLE", "关系型数据库"),

    STARROCKS("STARROCKS", "大数据存储");
    /**
     * Type name
     */
    public String name;

    /**
     * Classifier
     */
    public String classifier;
    ExchangisDataSourceType(String name, String classifier) {
        this.name = name;
        this.classifier = classifier;
    }
}
