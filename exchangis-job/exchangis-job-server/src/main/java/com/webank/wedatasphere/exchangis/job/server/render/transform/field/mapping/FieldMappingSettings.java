package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformSettings;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

import java.util.ArrayList;
import java.util.List;

public class FieldMappingSettings extends TransformSettings {
    /**
     * Add enable switch
     */
    private boolean addEnable;

    /**
     * Transform function enable switch
     */
    private boolean transformEnable;
    /**
     * Source fields
     */
    private List<FieldColumn> sourceFields = new ArrayList<>();

    /**
     * Sink fields
     */
    private List<FieldColumn> sinkFields = new ArrayList<>();

    /**
     * Mapping column
     */
    private List<FieldMappingColumn> deductions = new ArrayList<>();

    public boolean isAddEnable() {
        return addEnable;
    }

    public void setAddEnable(boolean addEnable) {
        this.addEnable = addEnable;
    }

    public boolean isTransformEnable() {
        return transformEnable;
    }

    public void setTransformEnable(boolean transformEnable) {
        this.transformEnable = transformEnable;
    }

    public List<FieldColumn> getSourceFields() {
        return sourceFields;
    }

    public void setSourceFields(List<FieldColumn> sourceFields) {
        this.sourceFields = sourceFields;
    }

    public List<FieldColumn> getSinkFields() {
        return sinkFields;
    }

    public void setSinkFields(List<FieldColumn> sinkFields) {
        this.sinkFields = sinkFields;
    }

    public List<FieldMappingColumn> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<FieldMappingColumn> deductions) {
        this.deductions = deductions;
    }
}
