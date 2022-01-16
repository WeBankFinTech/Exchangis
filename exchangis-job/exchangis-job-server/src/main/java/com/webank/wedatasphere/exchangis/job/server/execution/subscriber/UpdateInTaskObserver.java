package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;

import java.util.List;

public class UpdateInTaskObserver extends AbstractTaskObserver<LaunchedExchangisTask> {

    @Override
    protected List<LaunchedExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException {
        return null;
    }


    @Override
    public void subscribe(List<LaunchedExchangisTask> publishedTasks) throws ExchangisTaskObserverException {

    }
}
