package com.webank.wedatasphere.exchangis.job.launcher.manager;

import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.linkis.LinkisExchangisTaskLauncher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkisExchangisTaskLaunchManager implements ExchangisTaskLaunchManager {

    private final Map<String, ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask>> launchers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LinkisExchangisTaskLauncher linkisExchangisJobLauncher = new LinkisExchangisTaskLauncher();
        linkisExchangisJobLauncher.init(this);
        this.registerTaskLauncher(linkisExchangisJobLauncher);
    }


    @Override
    public void registerTaskLauncher(ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> taskLauncher) {
        this.launchers.put(taskLauncher.name().toUpperCase(Locale.ROOT), taskLauncher);
    }

    @Override
    public void unRegisterTaskLauncher(String launcherName) {
        this.launchers.remove(launcherName.toUpperCase(Locale.ROOT));
    }

    @Override
    public ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> getTaskLauncher(String launcherName) {
        return this.launchers.get(launcherName.toUpperCase(Locale.ROOT));
    }
}
