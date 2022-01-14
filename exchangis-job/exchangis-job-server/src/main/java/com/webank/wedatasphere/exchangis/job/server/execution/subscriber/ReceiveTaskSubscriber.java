package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Default implements
 */
@Component
public class ReceiveTaskSubscriber extends AbstractTaskObserver {


    @Override
    protected List<LaunchableExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException {
        return null;
    }


    @Override
    public void subscribe(List<LaunchableExchangisTask> publishedTasks) throws ExchangisTaskObserverException {

    }
}
