package com.webank.wedatasphere.exchangis.datasource.core.domain;

/**
 * Model relation
 */
public class DataSourceModelRelationDTO extends DataSourceModelRelation {

    private String modelName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public DataSourceModelRelationDTO(String modelName) {
        this.modelName = modelName;
    }

    public DataSourceModelRelationDTO() {
    }
}
