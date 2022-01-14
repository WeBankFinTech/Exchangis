package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchableExchangisTask;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkisExchangisJobLaunchManager implements ExchangisJobLaunchManager<LaunchableExchangisTask> {

    private final Map<String, ExchangisTaskLauncher<LaunchableExchangisTask>> launchers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LinkisExchangisTaskLanuncher linkisExchangisJobLanuncher = new LinkisExchangisTaskLanuncher();
        linkisExchangisJobLanuncher.init(this);
        this.registerJobLauncher(linkisExchangisJobLanuncher);
    }

    @Override
    public void registerJobLauncher(ExchangisTaskLauncher<LaunchableExchangisTask> jobLauncher) {
        this.launchers.put(jobLauncher.name().toUpperCase(Locale.ROOT), jobLauncher);
    }

    @Override
    public void unRgisterJobLauncher(String launcherName) {
        this.launchers.remove(launcherName.toUpperCase(Locale.ROOT));
    }

    @Override
    public ExchangisTaskLauncher<LaunchableExchangisTask> getJoblauncher(String launcherName) {
        return this.launchers.get(launcherName.toUpperCase(Locale.ROOT));
    }
}
