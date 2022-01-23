package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;

/**
 * To be hold by top level
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LaunchedExchangisTask extends LaunchedExchangisTaskEntity{
    public LaunchedExchangisTask(LaunchableExchangisTask launchableExchangisTask) {
        super(launchableExchangisTask);
    }

    @JsonIgnore
    private AccessibleLauncherTask launcherTask;

    public LaunchedExchangisTask(){

    }

    public AccessibleLauncherTask getLauncherTask() {
        return launcherTask;
    }

    public void setLauncherTask(AccessibleLauncherTask launcherTask) {
        this.launcherTask = launcherTask;
    }


}
