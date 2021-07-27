package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.List;

public class TableElementUI implements ElementUI {

    private String field;
    private String icon;
    private String content;
    private List<TableElementHeadUI> headers;

    public void setField(String field) {
        this.field = field;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeaders(List<TableElementHeadUI> headers) {
        this.headers = headers;
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
        return ElementUI.TABLE;
    }

    @Override
    public String getUIContent() {
        return this.content;
    }

    public List<TableElementHeadUI> getHeaders() {
        return headers;
    }
}
