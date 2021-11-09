package com.webank.wedatasphere.exchangis.job.launcher.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Launcher job builder
 */

public class ExchangisLauncherJobBuilder extends AbstractExchangisJobBuilder<ExchangisEngineJob, ExchangisLauncherJob> {

    @Override
    public ExchangisLauncherJob buildJob(ExchangisEngineJob inputJob, ExchangisLauncherJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {

        return null;
    }
}
