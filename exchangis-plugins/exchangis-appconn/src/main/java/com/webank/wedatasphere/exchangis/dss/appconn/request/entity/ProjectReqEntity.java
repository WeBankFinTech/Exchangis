package com.webank.wedatasphere.exchangis.dss.appconn.request.entity;

import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * Request entity of project
 */
public class ProjectReqEntity {

    private Long id;

    private String projectName;

    private String description;

    /**
     * Request domain
     */
    private String domain = Constraints.DOMAIN_NAME;
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
    private String labels;

    public ProjectReqEntity(){

    }

    public ProjectReqEntity(String owner, String projectName, String description, Map<String, Object> source){
        this.projectName = projectName;
        this.description = description;
        this.source = source;
        setEditUsers(owner);
        setViewUsers(owner);
        setExecUsers(owner);
    }

    public ProjectReqEntity(Long id, String editUsers, String viewUsers, String execUsers, String projectName, String description, Map<String, Object> source){
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.source = source;
        setEditUsers(editUsers);
        setViewUsers(viewUsers);
        setExecUsers(execUsers);
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

    public String getDomain() {
        return domain;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
