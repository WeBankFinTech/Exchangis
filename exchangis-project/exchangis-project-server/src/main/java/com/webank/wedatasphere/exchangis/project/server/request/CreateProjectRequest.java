package com.webank.wedatasphere.exchangis.project.server.request;

public class CreateProjectRequest {

    private String dssProjectId;

    private String workspaceName;

    private String projectName;

    private String description;

    private String tags;

    private String editUsers;

    private String viewUsers;

    private String execUsers;

    public String getDssProjectId() {
        return dssProjectId;
    }

    public void setDssProjectId(String dssProjectId) {
        this.dssProjectId = dssProjectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(String editUsers) {
        this.editUsers = editUsers;
    }

    public String getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(String viewUsers) {
        this.viewUsers = viewUsers;
    }

    public String getExecUsers() {
        return execUsers;
    }

    public void setExecUsers(String execUsers) {
        this.execUsers = execUsers;
    }
}
