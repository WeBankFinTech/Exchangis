package com.webank.wedatasphere.exchangis.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("exchangis_job_ds_bind")
public class ExchangisJobDsBind {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("job_id")
    private Long jobId;

    private Integer taskIndex;

    private Long sourceDsId;

    /**
     * Source data source name
     */
    private String sourceDsName;

    private Long sinkDsId;

    /**
     * Sink data source name
     */
    private String sinkDsName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(Integer taskIndex) {
        this.taskIndex = taskIndex;
    }

    public Long getSourceDsId() {
        return sourceDsId;
    }

    public void setSourceDsId(Long sourceDsId) {
        this.sourceDsId = sourceDsId;
    }

    public Long getSinkDsId() {
        return sinkDsId;
    }

    public void setSinkDsId(Long sinkDsId) {
        this.sinkDsId = sinkDsId;
    }

    public String getSourceDsName() {
        return sourceDsName;
    }

    public void setSourceDsName(String sourceDsName) {
        this.sourceDsName = sourceDsName;
    }

    public String getSinkDsName() {
        return sinkDsName;
    }

    public void setSinkDsName(String sinkDsName) {
        this.sinkDsName = sinkDsName;
    }
}
