package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkisExchangisTaskLaunchManager implements ExchangisTaskLaunchManager {

    private final Map<String, ExchangisTaskLauncher<LaunchableExchangisTask>> launchers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LinkisExchangisTaskLauncher linkisExchangisJobLauncher = new LinkisExchangisTaskLauncher();
        linkisExchangisJobLauncher.init(this);
        this.registerTaskLauncher(linkisExchangisJobLauncher);
    }


    @Override
    public void registerTaskLauncher(ExchangisTaskLauncher<LaunchableExchangisTask> taskLauncher) {
        this.launchers.put(taskLauncher.name().toUpperCase(Locale.ROOT), taskLauncher);
    }

    @Override
    public void unRegisterTaskLauncher(String launcherName) {
        this.launchers.remove(launcherName.toUpperCase(Locale.ROOT));
    }

    @Override
    public ExchangisTaskLauncher<LaunchableExchangisTask> getTaskLauncher(String launcherName) {
        return this.launchers.get(launcherName.toUpperCase(Locale.ROOT));
    }
}
