package com.webank.wedatasphere.exchangis.job.server.render.transform.field;

/**
 * Field column
 */
public class FieldColumn {

    /**
     * Field name
     */
    protected String name;

    /**
     * Field type
     */
    protected String type;

    /**
     * Field index
     */
    protected int fieldIndex;

    public FieldColumn(){

    }

    public FieldColumn(String name, String type, int fieldIndex){
        this.name = name;
        this.type = type;
        this.fieldIndex = fieldIndex;
    }

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

}
