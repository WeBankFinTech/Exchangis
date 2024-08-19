package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Just run when startup, recover the running launched task
 */
@Component
public class UpdateInTaskObserver extends AbstractTaskObserver<LaunchedExchangisTaskEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateInTaskObserver.class);

    private long startupTime = -1;

    @Override
    public String getInstance() {
        return EnvironmentUtils.getServerAddress();
    }

    @Override
    protected boolean nextPublish(int publishedSize, long observerWait) {
        return publishedSize > 0;
    }

    @Override
    protected List<LaunchedExchangisTaskEntity> onPublish(String instance, int batchSize) throws ExchangisTaskObserverException {
        if (observerService == null){
            return null;
        }
        if (this.lastPublishTime <= 0){
            // Get the server startup time
            startupTime = EnvironmentUtils.getStartupTime();
            LOG.info("Container startup time: {}", startupTime);
        }
        Date expireTime = new Date(startupTime);
        List<LaunchedExchangisTaskEntity> tasks = observerService.onPublishLaunchedTaskInExpire(instance, expireTime, batchSize);
        if (!tasks.isEmpty()) {
            LOG.info("Publish the launched tasks expired with 'Running' status from database, size: [{}], last_task_id: [{}], expire_time: [{}]",
                    tasks.size(), tasks.get(0).getId(), expireTime);
        } else {
            // shutdown the observer
            LOG.info("Stop to scan launched tasks expired with 'Running' status");
            this.isShutdown = true;
        }
        return tasks;
    }


    @Override
    public int subscribe(List<LaunchedExchangisTaskEntity> publishedTasks) throws ExchangisTaskObserverException {
        int count = 0;
        for (LaunchedExchangisTaskEntity task : publishedTasks){
            // Drop the related launched task in version
            if (observerService.subscribe(task)){
                count ++;
            }
        }
        return count;
    }

}
