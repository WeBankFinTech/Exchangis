package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;

/**
 * To be hold by top level
 */
public class LaunchedExchangisTask extends LaunchedExchangisTaskEntity {
    public LaunchedExchangisTask(LaunchableExchangisTask launchableExchangisTask) {
        super(launchableExchangisTask);
    }
}
