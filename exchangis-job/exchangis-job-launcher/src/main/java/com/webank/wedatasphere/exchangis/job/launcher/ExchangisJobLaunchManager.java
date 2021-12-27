package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.launcher.builder.ExchangisLauncherJob;

public interface ExchangisJobLaunchManager<T extends ExchangisLauncherJob> {

    void registerJobLauncher(ExchangisJobLauncher<T> jobLauncher);

    void unRgisterJobLauncher(String launcherName);

    ExchangisJobLauncher<T> getJoblauncher(String launcherName);

}
