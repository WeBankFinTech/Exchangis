package com.webank.wedatasphere.exchangis.datasource.core.ui;

public interface ElementUI {
    String TEXTAREA = "TEXTAREA";
    String INPUT = "INPUT";
    String OPTION = "OPTION";

    String getField();

    String getLabel();

    // 类型
    String getType();

    Integer getSort();

    String getValue();

}
