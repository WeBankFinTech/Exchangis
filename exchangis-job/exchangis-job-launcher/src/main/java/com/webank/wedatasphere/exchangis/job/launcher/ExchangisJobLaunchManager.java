package com.webank.wedatasphere.exchangis.job.launcher;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

public interface ExchangisJobLaunchManager<T extends LaunchableExchangisTask> {

    void registerJobLauncher(ExchangisTaskLauncher<T> jobLauncher);

    void unRgisterJobLauncher(String launcherName);

    ExchangisTaskLauncher<T> getJoblauncher(String launcherName);

}
