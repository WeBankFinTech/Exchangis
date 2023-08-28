package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

/**
 * Mapping column
 */
public class FieldMappingColumn {

    /**
     * Source column
     */
    private FieldColumn source;

    /**
     * Sink column
     */
    private FieldColumn sink;

    /**
     * Delete enable switch
     */
    private boolean deleteEnable;

    public FieldMappingColumn(){

    }

    public FieldMappingColumn(FieldColumn source, FieldColumn sink, boolean deleteEnable){
        this.source = source;
        this.sink = sink;
        this.deleteEnable = deleteEnable;
    }

    public FieldColumn getSource() {
        return source;
    }

    public void setSource(FieldColumn source) {
        this.source = source;
    }

    public FieldColumn getSink() {
        return sink;
    }

    public void setSink(FieldColumn sink) {
        this.sink = sink;
    }

    public boolean isDeleteEnable() {
        return deleteEnable;
    }

    public void setDeleteEnable(boolean deleteEnable) {
        this.deleteEnable = deleteEnable;
    }
}
