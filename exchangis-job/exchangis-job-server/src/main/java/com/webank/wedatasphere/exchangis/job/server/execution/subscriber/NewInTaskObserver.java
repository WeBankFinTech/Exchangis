package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Subscribe the new task from database and then submit to scheduler
 */
@Component
public class NewInTaskObserver extends CacheInTaskObserver {

    public NewInTaskObserver(int batchSize, int cacheSize) {
        super(batchSize, cacheSize);
    }

    @Override
    protected List<LaunchableExchangisTask> onPublishNext(int batchSize) {

        return null;
    }


    @Override
    public void subscribe(List<LaunchableExchangisTask> publishedTasks) {

    }
}
