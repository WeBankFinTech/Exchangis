package com.webank.wedatasphere.exchangis.datasource.vo;

public class FieldMappingVO {

    private String sourceTypeId;
    private Long sourceDataSourceId;
    private String sourceDataBase;
    private String sourceTable;

    private String sinkTypeId;
    private Long sinkDataSourceId;
    private String sinkDataBase;
    private String sinkTable;

    public String getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public Long getSourceDataSourceId() {
        return sourceDataSourceId;
    }

    public void setSourceDataSourceId(Long sourceDataSourceId) {
        this.sourceDataSourceId = sourceDataSourceId;
    }

    public String getSourceDataBase() {
        return sourceDataBase;
    }

    public void setSourceDataBase(String sourceDataBase) {
        this.sourceDataBase = sourceDataBase;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getSinkTypeId() {
        return sinkTypeId;
    }

    public void setSinkTypeId(String sinkTypeId) {
        this.sinkTypeId = sinkTypeId;
    }

    public Long getSinkDataSourceId() {
        return sinkDataSourceId;
    }

    public void setSinkDataSourceId(Long sinkDataSourceId) {
        this.sinkDataSourceId = sinkDataSourceId;
    }

    public String getSinkDataBase() {
        return sinkDataBase;
    }

    public void setSinkDataBase(String sinkDataBase) {
        this.sinkDataBase = sinkDataBase;
    }

    public String getSinkTable() {
        return sinkTable;
    }

    public void setSinkTable(String sinkTable) {
        this.sinkTable = sinkTable;
    }
}
