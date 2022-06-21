package com.webank.wedatasphere.exchangis.job.domain;


/**
 *
 */
public class ExchangisJobEntity extends ExchangisJobInfo{

    private Long projectId;

    private String source;

    private String modifyUser;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

}
