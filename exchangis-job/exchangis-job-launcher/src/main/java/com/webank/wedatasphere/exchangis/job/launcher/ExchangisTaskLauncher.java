package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

/**
 * Launcher interface
 * @param <T>
 */
public interface ExchangisTaskLauncher<T extends LaunchableExchangisTask> {

    String name();

    default void init(ExchangisTaskLaunchManager jobLaunchManager){}

    /**
     * Launch method
     * @param launchTask launchable task
     * @return launched task
     * @throws ExchangisTaskLaunchException exception in launching
     */
    LaunchedExchangisTask launch(T launchTask) throws ExchangisTaskLaunchException;

}
