package com.webank.wedatasphere.exchangis.job.server.render.transform;


import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class TransformRequestVo {
    /**
     * Engine type
     */
    private String engine;

    /**
     * Source type
     */
    @NotNull(message = "source type cannot be null (来源类型不能为空)")
    private String sourceTypeId;

    /**
     * Data source id (source direction)
     */
    @NotNull(message = "source id cannot be null (来源数据源ID不能为空）")
    private Long sourceDataSourceId;
    /**
     * Database (source direction)
     */
    private String sourceDataBase;

    /**
     * Table (source direction)
     */
    private String sourceTable;

    /**
     * Sink type id
     */
    @NotNull(message = "sink type cannot be null (目的类型不能为空）")
    private String sinkTypeId;

    /**
     * Sink data source id
     */
    @NotNull(message = "sink id cannot be null (目的数据源ID不能为空）")
    private Long sinkDataSourceId;

    /**
     * Database (sink direction)
     */
    private String sinkDataBase;

    /**
     * Table (sink direction)
     */
    private String sinkTable;

    /**
     * Labels
     */
    private Map<String, Object> labels = new HashMap<>();

    /**
     * Operate user
     */
    private String operator;
    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

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

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
