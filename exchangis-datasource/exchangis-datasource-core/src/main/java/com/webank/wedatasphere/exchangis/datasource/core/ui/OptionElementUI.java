package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.Collection;

public class OptionElementUI<V> implements ElementUI {

    private String field;
    private String label;
    private Collection<V> values;
    private V selected;

    public String getField() {
        return field;
    }

    @Override
    public String getIcon() {
        return "icon-option";
    }

    @Override
    public String getType() {
        return ElementUI.OPTION;
    }

    @Override
    public String getUIContent() {
        return "";
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<V> getValues() {
        return values;
    }

    public void setValues(Collection<V> values) {
        this.values = values;
    }

    public V getSelected() {
        return selected;
    }

    public void setSelected(V selected) {
        this.selected = selected;
    }
}
