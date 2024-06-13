package com.webank.wedatasphere.exchangis.project.entity.vo;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;

/**
 * Project data source query vo
 */
public class ProjectDsQueryVo extends PageQuery {

    /**
     * Project id
     */
    private Long projectId;
    /**
     * Data source name
     */
    private String name;
    /**
     * Data source type
     */
    private String type;

    public ProjectDsQueryVo() {
    }

    public ProjectDsQueryVo(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
