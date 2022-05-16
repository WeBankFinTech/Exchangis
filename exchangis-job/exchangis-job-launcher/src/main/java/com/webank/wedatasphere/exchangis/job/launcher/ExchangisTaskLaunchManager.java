package com.webank.wedatasphere.exchangis.job.launcher;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

/**
 * Task(Sub job) launcher
 */
public interface ExchangisTaskLaunchManager {

    void registerTaskLauncher(ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> taskLauncher);

    void unRegisterTaskLauncher(String launcherName);

    ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> getTaskLauncher(String launcherName);

}
