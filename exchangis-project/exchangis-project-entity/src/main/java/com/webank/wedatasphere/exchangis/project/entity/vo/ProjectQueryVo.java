package com.webank.wedatasphere.exchangis.project.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.project.entity.domain.ProjectPageQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Query vo
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectQueryVo extends ProjectPageQuery {

    public ProjectQueryVo(){
    }

    private List<Long> projectIds;

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    private Map<String, Object> labels;

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public ProjectQueryVo(String name, Integer current, Integer size){
        this.name = name;
        this.current = Optional.ofNullable(current).orElse(1);
        this.size = Optional.ofNullable(size).orElse(10);
    }

    public ProjectQueryVo(Map<String, Object> labels, String name){
        this.labels = labels;
        this.name = name;
    }

}
