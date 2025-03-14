package com.webank.wedatasphere.exchangis.job.domain;


/**
 *
 */
public class ExchangisJobEntity extends ExchangisJobInfo{

    private Long projectId;

    private String projectName;

    private String source;

    private String modifyUser;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    @Override
    public String toString() {
        return "ExchangisJobEntity{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", source='" + source + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", jobContent='" + jobContent + '\'' +
                ", executeUser='" + executeUser + '\'' +
                ", jobParams='" + jobParams + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                ", jobType='" + jobType + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", engineType='" + engineType + '\'' +
                ", jobLabel='" + jobLabel + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", createUser='" + createUser + '\'' +
                '}';
    }
}
