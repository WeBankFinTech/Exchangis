package com.webank.wedatasphere.exchangis.job.domain;


import java.util.Date;

/**
 * Basic job info
 */
public class ExchangisJobBase {

    /**
     * Identify
     */
    protected Long id;

    /**
     * JobName
     */
    protected String jobName;

    /**
     * Description
     */
     protected String jobDesc;

    /**
     * Create time
     */
     protected Date createTime;

    /**
     * Modify time
     */
    protected Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

}
