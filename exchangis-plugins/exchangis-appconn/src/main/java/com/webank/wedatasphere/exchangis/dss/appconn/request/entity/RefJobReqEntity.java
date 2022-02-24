package com.webank.wedatasphere.exchangis.dss.appconn.request.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Ref job entity
 */
public class RefJobReqEntity {

    private Long id;
    /**
     * Project id in thirty system
     */
    private Long projectId;

    /**
     * Job type
     */
    private String jobType;

    /**
     * Engine Type
     */
    private String engineType;

    /**
     * Job labels
     */
    private String jobLabels;

    /**
     * Job description
     */
    private String jobDesc;

    /**
     * Job name
     */
    private String jobName;

    private Map<String, Object> source = new HashMap<>();;
    public RefJobReqEntity(String jobName, String jobDesc,
                           String jobType, String engineType,
                           Long projectId){
        this.jobName = jobName;
        this.jobDesc = jobDesc;
        this.jobType = jobType;
        this.engineType = engineType;
        this.projectId = projectId;
    }
    public RefJobReqEntity(){

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

    public void setJobLabels(String jobLabels) {
        this.jobLabels = jobLabels;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
