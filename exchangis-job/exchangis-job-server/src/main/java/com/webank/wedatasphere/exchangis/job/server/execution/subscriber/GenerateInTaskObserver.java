package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Just run when startup, recover the init launched job
 */
@Component
public class GenerateInTaskObserver extends AbstractTaskObserver<LaunchedExchangisJobEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateInTaskObserver.class);


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
    protected List<LaunchedExchangisJobEntity> onPublish(String instance, int batchSize) throws ExchangisTaskObserverException {
        if (observerService == null){
            return null;
        }
        if (this.lastPublishTime <= 0){
            // Get the server startup time
            startupTime = EnvironmentUtils.getStartupTime();
            LOG.info("Container startup [time: {}]", startupTime);
        }
        Date expireTime = new Date(startupTime);
        List<LaunchedExchangisJobEntity> jobs = observerService.onPublishLaunchedJobInExpire(instance,
                expireTime, batchSize);
        if (!jobs.isEmpty()) {
            LOG.info("Publish the launched jobs expired with 'Inited' status from database, size: [{}], last_task_id: [{}], expire_time: [{}]",
                    jobs.size(), jobs.get(0).getId(), expireTime);
        } else {
            // shutdown the observer
            LOG.info("Stop to scan launched jobs expired with 'Inited' status");
            this.isShutdown = true;
        }
        return jobs;
    }

    @Override
    public int subscribe(List<LaunchedExchangisJobEntity> publishedJobs) throws ExchangisTaskObserverException {
        int count = 0;
        for (LaunchedExchangisJobEntity jobs : publishedJobs){
            // Drop the related launched task in version
            if (observerService.subscribe(jobs)){
                count ++;
            }
        }
        return count;
    }
}
