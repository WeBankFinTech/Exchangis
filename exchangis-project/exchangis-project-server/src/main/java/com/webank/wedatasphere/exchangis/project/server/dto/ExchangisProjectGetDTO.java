package com.webank.wedatasphere.exchangis.project.server.dto;

import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;

import java.util.Optional;

public class ExchangisProjectGetDTO {

    public ExchangisProjectGetDTO() {

    }

    public  ExchangisProjectGetDTO(ExchangisProject project) {
        if (null == project) {
            return;
        }
        this.setId(String.valueOf(project.getId()));
        Optional.ofNullable(project.getDssProjectId()).ifPresent(dssProjectId -> {
            this.setDssProjectId(String.valueOf(project.getDssProjectId()));
        });
        this.setName(project.getName());
        this.setWorkspaceName(project.getWorkspaceName());
        this.setDescription(project.getDescription());
        this.setTags(project.getTags());
        this.setEditUsers(project.getEditUsers());
        this.setViewUsers(project.getViewUsers());
        this.setExecUsers(project.getExecUsers());
    }

    private String id;
    private String dssProjectId;
    private String name;
    private String workspaceName;
    private String description;
    private String tags;
    private String editUsers;
    private String viewUsers;
    private String execUsers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDssProjectId() { return dssProjectId; }

    public void setDssProjectId(String dssProjectId) { this.dssProjectId = dssProjectId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkspaceName() { return workspaceName; }

    public void setWorkspaceName(String workspaceName) { this.workspaceName = workspaceName; }

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
