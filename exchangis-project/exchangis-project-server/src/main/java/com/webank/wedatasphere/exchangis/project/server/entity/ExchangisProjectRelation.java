package com.webank.wedatasphere.exchangis.project.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("exchangis_project_relation")
public class ExchangisProjectRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private Long projectId;
    @TableField("node_id")
    private Long nodeId;
    @TableField("project_version")
    private String projectVersion;
    @TableField("flow_version")
    private String flowVersion;
    @TableField("resource_id")
    private Long resourceId;
    @TableField("version")
    private String version;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }

    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getNodeId() { return nodeId; }

    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    public String getProjectVersion() { return projectVersion; }

    public void setProjectVersion(String projectVersion) { this.projectVersion = projectVersion; }

    public String getFlowVersion() { return flowVersion; }

    public void setFlowVersion(String flowVersion) { this.flowVersion = flowVersion; }

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }
}
