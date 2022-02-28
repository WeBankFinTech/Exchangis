package com.webank.wedatasphere.exchangis.project.server.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * View object
 * TODO @JsonInclude(JsonInclude.Include.NON_EMPTY)
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class  ExchangisProjectInfo {
    /**
     * ID
     */
    @NotBlank(message = "Project id cannot be null (工程ID不能为空)", groups = UpdateGroup.class)
    private Long id;

    /**
     * Project name
     */
    @NotBlank(message = "Project name cannot be null (工程名不能为空)")
    @Size(max = 64, message = "Length of project name should be less than 64 (工程名长度不超过64)")
    @JsonAlias("projectName")
    private String name;

    /**
     * Description
     */
    @Size(max = 200, message = "Length of desc should be less than 200 (描述长度不超过200)")
    private String description;

    /**
     * Request domain
     */
    private String domain;

    /**
     * Information from the dss
     */
    private Map<String, Object> source = new HashMap<>();

    /**
     * User has the edit permission
     */
    private String editUsers;

    /**
     * User has the view permission
     */
    private String viewUsers;

    /**
     * User has the execute permission
     */
    private String execUsers;

    /**
     * labels
     */
    @JsonAlias("tags")
    private String labels;

    /**
     * Create user
     */
    private String createUser;

    /**
     * Create time
     */
    private Date createTime;

    public ExchangisProjectInfo(){

    }

    public ExchangisProjectInfo(ExchangisProject project){
        this.setId(project.getId());
        this.setName(project.getName());
        this.setDescription(project.getDescription());
        this.setDomain(project.getDomain());
        this.setLabels(project.getLabels());
        this.setCreateUser(project.getCreateUser());
        this.setCreateTime(project.getCreateTime());
    }
    public String getId() {
        return id + "";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
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

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Deprecated
    public String getTags(){
        return Objects.nonNull(labels)? labels : "";
    }
}
