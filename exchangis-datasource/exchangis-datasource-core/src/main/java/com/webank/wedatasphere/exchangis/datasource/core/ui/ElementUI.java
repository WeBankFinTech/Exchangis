package com.webank.wedatasphere.exchangis.datasource.core.ui;

public interface ElementUI<T> {
    String TEXTAREA = "TEXTAREA";
    String INPUT = "INPUT";
    String OPTION = "OPTION";
    String MAP = "MAP";

    String getField();

    String getLabel();

    // 类型
    String getType();

    Integer getSort();

    T getValue();

    T getDefaultValue();

}
