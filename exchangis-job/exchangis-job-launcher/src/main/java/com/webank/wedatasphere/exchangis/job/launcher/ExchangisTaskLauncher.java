package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

import java.util.Map;

/**
 * Launcher interface
 * @param <T>
 */
public interface ExchangisTaskLauncher<T extends LaunchableExchangisTask, U extends LaunchedExchangisTask> {

    String name();

    default void init(ExchangisTaskLaunchManager jobLaunchManager){}

    /**
     * Build launcher task (accessible) form launched task entity
     * @param launchedTask launched task
     * @return
     */
    AccessibleLauncherTask launcherTask(U launchedTask) throws ExchangisTaskLaunchException;
    /**
     * Launch method
     * @param launchableTask launchable task
     * @return launched task
     * @throws ExchangisTaskLaunchException exception in launching
     */
    LaunchedExchangisTask launch(T launchableTask) throws ExchangisTaskLaunchException;

}
