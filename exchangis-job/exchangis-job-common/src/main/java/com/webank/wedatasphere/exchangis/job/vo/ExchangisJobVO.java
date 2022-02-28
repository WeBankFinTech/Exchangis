package com.webank.wedatasphere.exchangis.job.vo;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExchangisJobVO {

    /**
     * Job id
     */
    private Long id;

    /**
     * Project id
     */
    private Long projectId;

    /**
     * Job type
     */
    private String jobType;

    /**
     * Engine type
     */
    private String engineType;

    /**
     * Job labels
     */
    private String jobLabels;

    /**
     * Job name
     */
    private String jobName;

    /**
     * Job desc
     */
    private String jobDesc;

    /**
     * Content
     */
    private String content;

    /**
     * Execute user
     */
    @JsonProperty("proxyUser")
    private String executeUser;

    /**
     * Execute node
     */
    @Deprecated
    private String executeNode;

    /**
     * Store in source
     */
    private String syncType;

    /**
     * Job params
     */
    private String jobParams;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Create user
     */
    private String createUser;

    /**
     * Modify time (last_update_time)
     */
    private Date modifyTime;

    /**
     * Modify user
     */
    private String modifyUser;

    /**
     * Source map
     */
    private Map<String, Object> source = new HashMap<String, Object>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        //source.put("jobDesc", jobDesc);
        this.jobDesc = jobDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        //source.put("modifyTime", modifyTime);
        this.modifyTime = modifyTime;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        //source.put("jobType", jobType);
        this.jobType = jobType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getJobLabels() {
        return jobLabels;
    }

    public void setJobLabels(String jobLabel) {
        this.jobLabels = jobLabel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExecuteNode() {
       Object executeNode = source.get("executeNode");
       return Objects.nonNull(executeNode)? String.valueOf(executeNode) : null;
    }

    public void setExecuteNode(String executeNode) {
        source.put("executeNode", executeNode);
    }

    public String getSyncType() {
        Object syncType = source.get("syncType");
        return null;
    }

    public void setSyncType(String syncType) {
        source.put("syncType", syncType);
    }

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }
}
