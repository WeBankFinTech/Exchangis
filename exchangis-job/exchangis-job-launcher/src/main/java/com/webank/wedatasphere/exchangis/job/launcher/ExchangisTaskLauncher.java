package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

public interface ExchangisTaskLauncher<T extends LaunchableExchangisTask> {

    String name();

    default void init(ExchangisTaskLaunchManager jobLaunchManager){}

    LaunchedExchangisTask launch(T launchTask) throws ExchangisJobException;

}
