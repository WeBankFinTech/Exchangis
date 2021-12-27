package com.webank.wedatasphere.exchangis.project.server.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ExchangisProjectRelationDTO {

    private String id;
    @NotNull
    @NotEmpty
    private String projectId;
    @NotNull
    @NotEmpty
    private String nodeId;
    private String projectVersion;
    private String flowVersion;
    private String resourceId;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getFlowVersion() {
        return flowVersion;
    }

    public void setFlowVersion(String flowVersion) {
        this.flowVersion = flowVersion;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
