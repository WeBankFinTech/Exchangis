package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.GenericExchangisJob;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Job could be executed
 */
public class LaunchableExchangisJob extends GenericExchangisJob {

    /**
     * Job info
     */
    private ExchangisJobInfo exchangisJobInfo;

    /**
     * Execution id
     */
    private String jobExecutionId;

    /**
     * Execute user
     */
    private String execUser;

    /**
     *  Job params (JSON)
     */
    protected String jobParams;

    /**
     * Job parameter map
     */
    @JsonIgnore
    private Map<String, String> jobParamsMap;

    private List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>();

    public ExchangisJobInfo getExchangisJobInfo() {
        return exchangisJobInfo;
    }

    public void setExchangisJobInfo(ExchangisJobInfo exchangisJobInfo) {
        this.exchangisJobInfo = exchangisJobInfo;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public List<LaunchableExchangisTask> getLaunchableExchangisTasks() {
        return launchableExchangisTasks;
    }

    public void setLaunchableExchangisTasks(List<LaunchableExchangisTask> launchableExchangisTasks) {
        this.launchableExchangisTasks = launchableExchangisTasks;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
        this.jobParamsMap = null;
    }

    public Map<String, String> getJobParamsMap() {
        if (null == this.jobParamsMap){
            if (StringUtils.isNotBlank(this.jobParams)){
                try {
                    this.jobParamsMap = Json.fromJson(this.jobParams, null);
                    return this.jobParamsMap;
                } catch (Exception e){
                    // Ignore the exception
                }
            }
            this.jobParamsMap = new HashMap<>();
        }
        return jobParamsMap;
    }

    public void setJobParamsMap(Map<String, String> jobParamsMap) {
        this.jobParamsMap = jobParamsMap;
    }
}
