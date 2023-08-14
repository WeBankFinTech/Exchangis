package com.webank.wedatasphere.exchangis.project.entity.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
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
public class ExchangisProjectInfo {
    /**
     * ID
     */
    @NotNull(message = "Project id cannot be null (工程ID不能为空)", groups = UpdateGroup.class)
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
    private String label;

    /**
     * Create user
     */
    private String createUser;

    /**
     * Create time
     */
    private Date createTime;

    private Map<String, Object> labels;

    private String privilege;

    private String privUser;

    public ExchangisProjectInfo(){

    }

    public ExchangisProjectInfo(ExchangisProject project){
        this.setId(project.getId());
        this.setName(project.getName());
        this.setDescription(project.getDescription());
        this.setDomain(project.getDomain());
        this.setLabel(project.getLabels());
        this.setPrivilege("");
        this.setPrivUser(project.getPrivUser());
        this.setExecUsers(project.getExecUsers());
        this.setViewUsers(project.getViewUsers());
        this.setEditUsers(project.getEditUsers());
        this.setCreateUser(project.getCreateUser());
        this.setCreateTime(project.getCreateTime());
    }

    public ExchangisProjectInfo(ExchangisProjectAppVo project){
        Map<String, Object> labels = new HashMap<>();
        //labels.put("route", project.getLabels());
        this.setName(project.getName());
        this.setDescription(project.getDescription());
        this.setDomain(project.getDomain());
        this.setSource(project.getSource());
        this.setPrivUser("");
        this.setEditUsers(project.getEditUsers());
        this.setViewUsers(project.getViewUsers());
        this.setExecUsers(project.getExecUsers());
        this.setLabel(project.getLabel());
        this.setCreateUser(project.getCreateUser());
        this.setCreateTime(project.getCreateTime());
        this.setLabels(project.getLabels());
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
        /*if(!privUser.equals(null)) {
            if (this.editUsers.contains(privUser)) {
                if (privilege.length() != 0) {
                    privilege += ",3";
                } else {
                    privilege += "3";
                }
            }
        }*/
    }

    public String getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(String viewUsers) {
        this.viewUsers = viewUsers;
        /*if(!privUser.equals(null)) {
            if (this.viewUsers.contains(privUser)) {
                if (privilege.length() != 0) {
                    privilege += ",1";
                } else {
                    privilege += "1";
                }
            }
        }*/
    }

    public String getExecUsers() {
        return execUsers;
    }

    public void setExecUsers(String execUsers) {
        this.execUsers = execUsers;
        /*if(!privUser.equals(null)) {
            if (this.execUsers.contains(privUser)) {
                if (privilege.length() != 0) {
                    privilege += ",2";
                } else {
                    privilege += "2";
                }
            }
        }*/
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        /*if(!privUser.equals(null)) {
            if (this.createUser.contains(privUser)) {
                if (privilege.length() != 0) {
                    privilege += ",0";
                } else {
                    privilege += "0";
                }
            }
        }*/
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Deprecated
    public String getTags(){
        return Objects.nonNull(label)? label : "";
    }

    public Map<String, Object> getLabels() {
            return labels;
        }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getPrivUser() {
        return privUser;
    }

    public void setPrivUser(String privUser) {
        this.privUser = privUser;
    }
}
