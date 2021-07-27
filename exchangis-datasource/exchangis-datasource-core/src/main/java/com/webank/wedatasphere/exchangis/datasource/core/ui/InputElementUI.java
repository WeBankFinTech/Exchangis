package com.webank.wedatasphere.exchangis.datasource.core.ui;

public class InputElementUI implements ElementUI {

    private String field;
    private String icon;
    private String content;

    public void setField(String field) {
        this.field = field;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public String getIcon() {
        return this.icon;
    }

    @Override
    public String getType() {
        return ElementUI.INPUT;
    }

    @Override
    public String getUIContent() {
        return this.content;
    }

}
