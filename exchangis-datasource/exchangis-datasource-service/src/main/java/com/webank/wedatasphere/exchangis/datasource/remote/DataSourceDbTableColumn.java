package com.webank.wedatasphere.exchangis.datasource.remote;

/**
 * Database table column
 */
public class DataSourceDbTableColumn {
    /**
     * Name
     */
    private String name;
    /**
     * Type
     */
    private String type;
    /**
     * Field index
     */
    private int fieldIndex;
    /**
     * Editable
     */
    private boolean fieldEditable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public boolean isFieldEditable() {
        return fieldEditable;
    }

    public void setFieldEditable(boolean fieldEditable) {
        this.fieldEditable = fieldEditable;
    }
}
