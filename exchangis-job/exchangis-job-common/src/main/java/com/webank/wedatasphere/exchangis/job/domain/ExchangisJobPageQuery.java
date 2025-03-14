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

    protected String dataSrcType;

    protected String dataDestType;

    protected String sourceSinkId;

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDataSrcType() {
        return dataSrcType;
    }

    public void setDataSrcType(String dataSrcType) {
        this.dataSrcType = dataSrcType;
    }

    public String getDataDestType() {
        return dataDestType;
    }

    public void setDataDestType(String dataDestType) {
        this.dataDestType = dataDestType;
    }

    public String getSourceSinkId() {
        return sourceSinkId;
    }

    public void setSourceSinkId(String sourceSinkId) {
        this.sourceSinkId = sourceSinkId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
