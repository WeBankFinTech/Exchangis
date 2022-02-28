package com.webank.wedatasphere.exchangis.job.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 任务表.
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@TableName("exchangis_job_info")
public class ExchangisJobVO {


    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long dssProjectId;

    private String dssProjectName;

    private String nodeId;

    private String nodeName;

    private String jobType;

    private String engineType;

    private String jobLabels;

    private String jobName;

    private String jobDesc;

    private String content;

    private String alarmUser;

    private Integer alarmLevel;

    private String proxyUser;

    private String executeNode;

    private String syncType;

    private String jobParams;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

    /*private Map<String, Object> source = new HashMap<>();

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
    }*/

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

    public Long getDssProjectId() { return dssProjectId; }

    public void setDssProjectId(Long dssProjectId) {
        //source.put("dssProjectId", dssProjectId);
        this.dssProjectId = dssProjectId; }


    public String getNodeName() { return nodeName; }

    public void setNodeName(String nodeName) {
        //source.put("nodeName", nodeName);
        this.nodeName = nodeName; }

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

    public String getAlarmUser() {
        return alarmUser;
    }

    public void setAlarmUser(String alarmUser) {
        //source.put("alarmUser", alarmUser);
        this.alarmUser = alarmUser;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        //source.put("alarmLevel", alarmLevel);
        this.alarmLevel = alarmLevel;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        //source.put("proxyUser", proxyUser);
        this.proxyUser = proxyUser;
    }

    public String getExecuteNode() {
        return executeNode;
    }

    public void setExecuteNode(String executeNode) {
        //source.put("executeNode", executeNode);
        this.executeNode = executeNode;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        //source.put("syncType", syncType);
        this.syncType = syncType;
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

    public String getDssProjectName() {
        return dssProjectName;
    }

    public void setDssProjectName(String dssProjectName) {
        //source.put("dssProjectName", dssProjectName);
        this.dssProjectName = dssProjectName;
    }

    public void setNodeId(String nodeId) {
        //source.put("nodeId", nodeId);
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "ExchangisJob{" + "id=" + id + ", projectId=" + projectId + ", jobName=" + jobName + ", jobType="
                + jobType
                + ", engineType=" + engineType + ", jobLabels=" + jobLabels + ", jobDesc=" + jobDesc + ", content="
                + content + ", alarmUser=" + alarmUser + ", alarmLevel=" + alarmLevel + ", proxyUser=" + proxyUser
                + ", executeNode=" + executeNode + ", syncType=" + syncType + ", jobParams=" + jobParams + ", createTime="
                + createTime + ", createUser=" + createUser + ", modifyTime=" + modifyTime + ", modifyUser=" + modifyUser
                + "}";
    }
}
