package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;

/**
 * Contain the content and parameters
 */
public class ExchangisJobInfo extends GenericExchangisJob {
    /**
     * Job content (JSON)
     */
    private String jobContent;

    /**
     * Execute user
     */
    private String executeUser;

    /**
     *  Job params (JSON)
     */
    private String jobParams;

    /**
     * Convert from view object
     * @param jobVo vo
     */
    public ExchangisJobInfo(ExchangisJobVO jobVo){
        this.id = jobVo.getId();
        this.name = jobVo.getJobName();
        this.engineType = jobVo.getEngineType();
        this.jobLabel = jobVo.getJobLabels();
        this.createTime = jobVo.getCreateTime();
        this.createUser = jobVo.getCreateUser();
        this.lastUpdateTime = jobVo.getModifyTime();
        this.jobContent = jobVo.getContent();
        this.executeUser = jobVo.getProxyUser();
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
}
