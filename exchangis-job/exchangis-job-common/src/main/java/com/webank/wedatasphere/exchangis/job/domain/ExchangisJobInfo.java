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
     * @param exchangisJobVO vo
     */
    public ExchangisJobInfo(ExchangisJobVO exchangisJobVO){

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
