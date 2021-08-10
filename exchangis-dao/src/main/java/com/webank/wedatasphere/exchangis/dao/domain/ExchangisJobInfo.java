package com.webank.wedatasphere.exchangis.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("exchangis_job_info")
public class ExchangisJobInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "job_name")
    private String jobName;

    @TableField(value = "job_desc")
    private String jobDesc;

    @TableField(value = "engine_type")
    private String engineType;

    @TableField(value = "alarm_user")
    private String alarmUser;

    @TableField(value = "alarm_level")
    private Integer alarmLevel;

    @TableField(value = "content")
    private String content;

    @TableField(value = "label_map")
    private String labelMap;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "modify_time")
    private Date modifyTime;

    @TableField(value = "create_user")
    private String createUser;

    @TableField(value = "modify_user")
    private String modifyUser;

    @TableField(value = "to_proxy_user")
    private String toProxyUser;

    @TableField(value = "exec_nodes")
    private String execNodes;

    @TableField(value = "last_launch_time")
    private Date lastLaunchTime;

    @TableField(value = "project_id")
    private Integer projectId;

    @TableField(value = "parent_id")
    private Long parentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getAlarmUser() {
        return alarmUser;
    }

    public void setAlarmUser(String alarmUser) {
        this.alarmUser = alarmUser;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabelMap() {
        return labelMap;
    }

    public void setLabelMap(String labelMap) {
        this.labelMap = labelMap;
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

    public String getToProxyUser() {
        return toProxyUser;
    }

    public void setToProxyUser(String toProxyUser) {
        this.toProxyUser = toProxyUser;
    }

    public String getExecNodes() {
        return execNodes;
    }

    public void setExecNodes(String execNodes) {
        this.execNodes = execNodes;
    }

    public Date getLastLaunchTime() {
        return lastLaunchTime;
    }

    public void setLastLaunchTime(Date lastLaunchTime) {
        this.lastLaunchTime = lastLaunchTime;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

}