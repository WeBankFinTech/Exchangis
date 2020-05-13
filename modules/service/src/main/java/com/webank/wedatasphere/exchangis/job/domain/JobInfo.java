/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.util.*;

/**
 * Created by devendeng on 2018/8/24.
 */

/**
 * Job configuration
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobInfo {
    private Long id;
    @NotBlank(message = "{udes.domain.jobInfo.name.notBlank}")
    @Size(max = 100, message = "{udes.domain.jobInfo.name.max}")
    private String jobName;
    private String jobCorn;

    /**
     * If is disposable
     */
    @JsonIgnore
    private Boolean disposable = false;

    /**
     * Engine type
     */
    private JobEngine engineType = JobEngine.DATAX;

    private String jobDesc;

    /**
     * Alarm user
     */
    private String alarmUser;

    /**
     * Alarm level
     */
    private Integer alarmLevel;
    /**
     * Retry count
     */
    @Min(value = 0, message = "错误重试不能小于0")
    private Integer failReteryCount;

    /**
     * Project id
     */
    @NotNull(message = "{udes.domain.jobInfo.project.id.notNull}")
    private Long projectId;

    /**
     * Project name
     */
    private String projectName;

    @Min(value = 0, message = "{udes.domain.jobInfo.timeout.min}")
    private Long timeout = 0L;
    /**
     * Source ID
     */
    @NotNull(message = "{udes.domain.jobInfo.dataSrcId.notNull}")
    private Long dataSrcId;

    private String dataSrcType;

    private String dataSrcOwner;
    /**
     * Dest ID
     */
    @NotNull(message = "{udes.domain.jobInfo.dataDestId.notNull}")
    private Long dataDestId;

    private String dataDestType;

    private String dataDestOwner;


    /**
     * Job config form
     */
    @Valid
    private JobConfForm config = new JobConfForm();
    /**
     * Job config string
     */
    @JsonIgnore
    private String jobConfig;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createUser;

    private String modifyUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    /**
     * last trigger time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastTriggerTime;
    /**
     * Job status
     */
    private String jobStatus;

    /**
     * User used to execute
     */
    private String execUser;

    /**
     * Nodes used to execute
     */
    private List<Integer> execNodes = new ArrayList<>();

    private List<String> execNodeNames = new ArrayList<>();

    /**
     * Job parameters
     */
    private Map<String, String> jobParams = new HashMap<>();
    /**
     * Sync method
     */
    private JobSyncEnum sync;

    /**
     * File attached to job(can be 'keytab' file or else)
     * for exchange job , size is 2
     */
    @JsonIgnore
    private File[] attachFiles = new File[2];

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

    public String getJobCorn() {
        return jobCorn;
    }

    public void setJobCorn(String jobCorn) {
        this.jobCorn = jobCorn;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getAlarmUser() {
        return alarmUser;
    }

    public void setAlarmUser(String alarmUser) {
        this.alarmUser = alarmUser;
    }

    public Integer getFailReteryCount() {
        return failReteryCount;
    }

    public void setFailReteryCount(Integer failReteryCount) {
        this.failReteryCount = failReteryCount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDataSrcOwner() {
        return dataSrcOwner;
    }

    public void setDataSrcOwner(String dataSrcOwner) {
        this.dataSrcOwner = dataSrcOwner;
    }

    public String getDataDestOwner() {
        return dataDestOwner;
    }

    public void setDataDestOwner(String dataDestOwner) {
        this.dataDestOwner = dataDestOwner;
    }

    public String getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(String jobConfig) {
        this.jobConfig = jobConfig;
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

    public  Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Long getDataSrcId() {
        return dataSrcId;
    }

    public void setDataSrcId(Long dataSrcId) {
        this.dataSrcId = dataSrcId;
    }

    public Long getDataDestId() {
        return dataDestId;
    }

    public void setDataDestId(Long dataDestId) {
        this.dataDestId = dataDestId;
    }

    public JobConfForm getConfig() {
        return config;
    }

    public void setConfig(JobConfForm config) {
        this.config = config;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public File[] getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(File[] attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String getDataSrcType() {
        return dataSrcType;
    }

    public void setDataSrcType(String dataSrcType) {
        this.dataSrcType = dataSrcType;
    }

    public String getDataDestType() {
        return dataDestType;
    }

    public void setDataDestType(String dataDestType) {
        this.dataDestType = dataDestType;
    }

    public Boolean getDisposable() {
        return disposable;
    }

    public void setDisposable(Boolean disposable) {
        this.disposable = disposable;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    public List<Integer> getExecNodes() {
        return execNodes;
    }

    public void setExecNodes(List<Integer> execNodes) {
        this.execNodes = execNodes;
    }

    public JobSyncEnum getSync() {
        return sync;
    }

    public void setSync(JobSyncEnum sync) {
        this.sync = sync;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Date getLastTriggerTime() {
        return lastTriggerTime;
    }

    public void setLastTriggerTime(Date lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public List<String> getExecNodeNames() {
        return execNodeNames;
    }

    public void setExecNodeNames(List<String> execNodeNames) {
        this.execNodeNames = execNodeNames;
    }

    public JobEngine getEngineType() {
        return engineType;
    }

    public void setEngineType(JobEngine engineType) {
        this.engineType = engineType;
    }


    public Map<String, String> getJobParams() {
        return jobParams;
    }

    public void setJobParams(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

    @Override
    public String toString() {
        return "JobInfo{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", jobCorn='" + jobCorn + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                ", alarmUser='" + alarmUser + '\'' +
                ", failReteryCount=" + failReteryCount +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", dataSrcOwner='" + dataSrcOwner + '\'' +
                ", dataDestOwner='" + dataDestOwner + '\'' +
                ", jobConfig='" + jobConfig + '\'' +
                ", createTime=" + createTime +
                ", createUser=" + createUser +
                ", modifyUser=" + modifyUser +
                ", modifyTime=" + modifyTime +
                ", jobStatus='" + jobStatus + '\'' +
                '}';
    }



}
