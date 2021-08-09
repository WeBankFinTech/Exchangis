package com.webank.wedatasphere.exchangis.datasource.core.ui;

public interface ElementUI {
    String FORM = "FORM";
    String TEXTAREA = "TEXTAREA";
    String INPUT = "INPUT";
    String OPTION = "OPTION";
    String TABLE = "TABLE";

    // 域标识、唯一性
    String getField();

    // 图标
    String getIcon();

    // 类型
    String getType();

    // 内容：JSON XML HTML CSS
    String getUIContent();
}
