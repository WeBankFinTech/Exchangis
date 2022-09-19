package com.webank.wedatasphere.exchangis.job.vo;



import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 *
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExchangisJobVo {

    /**
     * Job id
     */
    private Long id;

    /**
     * Project id
     */
    @NotNull(groups = InsertGroup.class, message = "Project id cannot be null (工程ID不能为空)")
    private Long projectId;

    /**
     * Job type
     */
    @NotBlank(message = "Job type cannot be null (任务类型不能为空)")
    @Size(max = 50, message= "Length of job type should be less than 50 (任务类型长度不超过50)")
    private String jobType;

    /**
     * Engine type
     */
    @NotBlank(message = "Engine type cannot be null (引擎类型不能为空)")
    @Size(max = 50, message = "Length of engine type should be less than 50 (引擎类型长度不超过50)")
    private String engineType;

    /**
     * Job labels
     */
    @Size(max = 200, message = "Length of labels should be less than 200 (标签长度不能超过200)")
    private String jobLabels;

    /**
     * Job name
     */
    @Size(max = 100, message = "Length of name should be less than 100 (名称长度不超过100)")
    @NotBlank(message = "Job name cannot be null (任务名不能为空)")
    private String jobName;

    /**
     * Job desc
     */
    @Size(max = 200, message = "Length of desc should be less than 200 (描述长度不超过200)")
    private String jobDesc;

    /**
     * Content
     */
    private String content;

    /**
     * Execute user
     */
    //@JsonProperty("proxyUser")
    private String executeUser;

    /**
     * Execute node
     */
    @Deprecated
    private String executeNode;

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

    private Map<String, Object> labels;

    private String proxyUser;

    public ExchangisJobVo(){

    }

    public ExchangisJobVo(ExchangisJobEntity jobEntity){
        if (Objects.nonNull(jobEntity)) {
            this.id = jobEntity.getId();
            this.projectId = jobEntity.getProjectId();
            this.engineType = jobEntity.getEngineType();
            this.jobDesc = jobEntity.getJobDesc();
            this.jobLabels = jobEntity.getJobLabel();
            this.jobName = jobEntity.getName();
            this.jobType = jobEntity.getJobType();
            this.createTime = jobEntity.getCreateTime();
            this.createUser = jobEntity.getCreateUser();
            this.modifyTime = jobEntity.getLastUpdateTime();
            this.jobParams = jobEntity.getJobParams();
            this.executeUser = jobEntity.getExecuteUser();
            this.proxyUser = jobEntity.getExecuteUser();
        }
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

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
        return Objects.nonNull(syncType)? String.valueOf(syncType) : null;
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

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source.putAll(source);
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }
}
