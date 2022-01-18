package com.webank.wedatasphere.exchangis.job.launcher;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

/**
 * Task(Sub job) launcher
 */
public interface ExchangisTaskLaunchManager {

    void registerTaskLauncher(ExchangisTaskLauncher<LaunchableExchangisTask> taskLauncher);

    void unRegisterTaskLauncher(String launcherName);

    ExchangisTaskLauncher<LaunchableExchangisTask> getTaskLauncher(String launcherName);

}
