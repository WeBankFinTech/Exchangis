package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;

/**
 * Contain the content and parameters
 */
public class ExchangisJobInfo extends GenericExchangisJob {
    /**
     * Job content (JSON)
     */
    protected String jobContent;

    /**
     * Execute user
     */
    protected String executeUser;

    /**
     *  Job params (JSON)
     */
    protected String jobParams;

    /**
     * Job description
     */
    protected String jobDesc;

    /**
     * Job type
     */
    protected String jobType;
    /**
     * Convert from view object
     * @param jobVo vo
     */
    public ExchangisJobInfo(ExchangisJobVo jobVo){
        this.id = jobVo.getId();
        this.name = jobVo.getJobName();
        this.engineType = jobVo.getEngineType();
        this.jobLabel = jobVo.getJobLabels();
        this.createTime = jobVo.getCreateTime();
        this.createUser = jobVo.getCreateUser();
        this.lastUpdateTime = jobVo.getModifyTime();
        this.jobContent = jobVo.getContent();
        this.executeUser = jobVo.getExecuteUser();
        this.jobParams = jobVo.getJobParams();
    }

    public ExchangisJobInfo(){

    }
    public String getJobContent() {
        return jobContent;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}
