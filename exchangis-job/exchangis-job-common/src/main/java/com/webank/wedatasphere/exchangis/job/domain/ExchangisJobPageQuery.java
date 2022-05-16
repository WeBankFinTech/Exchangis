package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;

/**
 * For querying page
 */
public class ExchangisJobPageQuery extends PageQuery {

    /**
     * Project id
     */
    protected Long projectId;

    /**
     * Job type
     */
    protected String jobType;

    /**
     * Job name
     */
    protected String jobName;

    protected String createUser;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
