package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implements
 */
@Component
public class ReceiveTaskSubscriber extends AbstractTaskObserver<LaunchedExchangisTask> {


    @Override
    protected List<LaunchedExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException {
        return new ArrayList<>();
    }


    @Override
    public void subscribe(List<LaunchedExchangisTask> publishedTasks) throws ExchangisTaskObserverException {

    }
}
