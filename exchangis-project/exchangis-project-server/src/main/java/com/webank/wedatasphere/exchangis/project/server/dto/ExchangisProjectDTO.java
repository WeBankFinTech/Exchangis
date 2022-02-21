package com.webank.wedatasphere.exchangis.project.server.dto;


import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangisProjectDTO {

    public ExchangisProjectDTO() {}

    public ExchangisProjectDTO(ExchangisProject project) {

        this.setId(String.valueOf(project.getId()));
        Optional.ofNullable(project.getDssProjectId()).ifPresent(dssProjectId -> {
            this.setDssProjectId(String.valueOf(dssProjectId));
        });
        this.setName(project.getName());
        this.setWorkspaceName(project.getWorkspaceName());
        this.setTags(project.getTags());
        this.setDescription(project.getDescription());
        this.setDomain(project.getDomain());
    }

    private String id;
    private String dssProjectId;
    private String name;
    private String workspaceName;
    private String description;
    private String tags;
    private String domain;

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

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }
}
