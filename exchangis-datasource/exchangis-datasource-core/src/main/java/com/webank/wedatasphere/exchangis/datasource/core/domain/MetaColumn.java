package com.webank.wedatasphere.exchangis.datasource.core.domain;

/**
 * Meta column
 */
public class MetaColumn {

    /**
     * Column index
     */
    private int index = -1;

    /**
     * Is primary key
     */
    private boolean primaryKey;

    /**
     * Name
     */
    private String name;

    /**
     * Type symbol
     */
    private String type;

    public MetaColumn(){

    }

    public MetaColumn(int index, String name, String type, boolean primaryKey){
        this.index = index;
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
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
}
