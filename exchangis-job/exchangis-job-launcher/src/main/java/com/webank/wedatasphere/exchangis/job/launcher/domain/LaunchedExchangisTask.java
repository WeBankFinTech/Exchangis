package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;

import java.util.Map;

/**
 * To be hold by top level
 */
public class LaunchedExchangisTask extends LaunchedExchangisTaskEntity implements AccessibleLaunchedExchangisTask {
    public LaunchedExchangisTask(LaunchableExchangisTask launchableExchangisTask) {
        super(launchableExchangisTask);
    }

    public LaunchedExchangisTask(){

    }


    @Override
    public Map<String, Object> callMetricsUpdate() {
        return null;
    }

    @Override
    public TaskStatus callStatusUpdate() {
        return null;
    }

    @Override
    public void kill() {

    }
}
