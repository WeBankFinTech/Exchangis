package com.webank.wedatasphere.exchangis.job.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.webank.wedatasphere.exchangis.job.enums.JobTypeEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangisJobBasicInfoDTO {

    private int id;

    private Long projectId;

    private Long dssProjectId;

    private String dssProjectName;

    private String nodeId;

    private String nodeName;

    private String name;

    private JobTypeEnum jobType;

    private String engineType;

    private String jobLabels;

    private String jobDesc;

    private String jobName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getDssProjectId() { return dssProjectId; }

    public void setDssProjectId(Long dssProjectId) { this.dssProjectId = dssProjectId; }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() { return nodeName; }

    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobTypeEnum getJobType() {
        return jobType;
    }

    public void setJobType(JobTypeEnum jobType) {
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

    public void setJobLabels(String jobLabels) {
        this.jobLabels = jobLabels;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getDssProjectName() {
        return dssProjectName;
    }

    public void setDssProjectName(String dssProjectName) {
        this.dssProjectName = dssProjectName;
    }
}
