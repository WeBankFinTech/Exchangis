package com.webank.wedatasphere.exchangis.datasource.core.domain;

public enum StructClassifier {

    STRUCTURED("结构化"),

    SEMI_STRUCTURED("半结构化"),

    NON_STRUCTURED("无结构化");

    public String name;

    StructClassifier(String name) {
        this.name = name;
    }
}
