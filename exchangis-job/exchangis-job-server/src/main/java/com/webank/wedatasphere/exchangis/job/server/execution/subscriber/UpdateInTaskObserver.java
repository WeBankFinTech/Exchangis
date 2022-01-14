package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;

import java.util.List;

public class UpdateInTaskObserver extends AbstractTaskObserver{

    @Override
    protected List<LaunchableExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException {
        return null;
    }

    @Override
    public void subscribe(List<LaunchableExchangisTask> publishedTasks) throws ExchangisTaskObserverException {

    }
}
