package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.entity.ExchangisLauncherJob;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkisExchangisJobLaunchManager implements ExchangisJobLaunchManager<ExchangisLauncherJob> {

    private final Map<String, ExchangisJobLauncher<ExchangisLauncherJob>> launchers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LinkisExchangisJobLanuncher linkisExchangisJobLanuncher = new LinkisExchangisJobLanuncher();
        linkisExchangisJobLanuncher.init(this);
        this.registerJobLauncher(linkisExchangisJobLanuncher);
    }

    @Override
    public void registerJobLauncher(ExchangisJobLauncher<ExchangisLauncherJob> jobLauncher) {
        this.launchers.put(jobLauncher.name().toUpperCase(Locale.ROOT), jobLauncher);
    }

    @Override
    public void unRgisterJobLauncher(String launcherName) {
        this.launchers.remove(launcherName.toUpperCase(Locale.ROOT));
    }

    @Override
    public ExchangisJobLauncher<ExchangisLauncherJob> getJoblauncher(String launcherName) {
        return this.launchers.get(launcherName.toUpperCase(Locale.ROOT));
    }
}
