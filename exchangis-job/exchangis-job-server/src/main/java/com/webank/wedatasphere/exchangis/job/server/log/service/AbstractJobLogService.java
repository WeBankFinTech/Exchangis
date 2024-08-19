package com.webank.wedatasphere.exchangis.job.server.log.service;

import com.google.common.cache.*;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.log.cache.AbstractJobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR;

/**
 * Abstract Job log service
 */
public abstract class AbstractJobLogService implements JobLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJobLogService.class);

    protected Cache<String, JobLogCache<String>> cacheHolder;

    private AbstractExchangisSchedulerTask cleaner;

    private volatile boolean cleanerOn;

    protected static class Constraints{
        public static final CommonVars<String> LOG_LOCAL_PATH = CommonVars.apply("wds.exchangis.job.log.local.path", "/data/bdp/dss/exchangis/main/logs");

        public static final CommonVars<Integer> lOG_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.log.cache.size", 15);

        public static final CommonVars<Integer> LOG_CACHE_EXPIRE_TIME_IN_SECONDS = CommonVars.apply("wds.exchangis.job.log.cache.expire.time-in-seconds", 5);

        public static final CommonVars<String> LOG_MULTILINE_PATTERN = CommonVars.apply("wds.exchangis.log.multiline.pattern", "^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
    }

    @Resource
    protected Scheduler scheduler;

    @Resource
    private LaunchedJobDao launchedJobDao;
    @PostConstruct
    public void init(){
        cleanerOn = true;
        cacheHolder = CacheBuilder.newBuilder().maximumSize(Constraints.lOG_CACHE_SIZE.getValue())
                .expireAfterAccess(Constraints.LOG_CACHE_EXPIRE_TIME_IN_SECONDS.getValue(), TimeUnit.SECONDS)
                .removalListener((RemovalListener<String, JobLogCache<String>>) removalNotification -> {
                    // Flush for expired
                    if (removalNotification.getCause() == RemovalCause.EXPIRED){
                        removalNotification.getValue().flushCache(true);
                    }
                })
                .build();
        cleaner = new AbstractExchangisSchedulerTask("Job-Log-Cache-Cleaner") {
            @Override
            public String getTenancy() {
                return "log";
            }

            @Override
            public String getName() {
                return getId();
            }

            @Override
            public JobInfo getJobInfo() {
                return null;
            }

            @Override
            protected void schedule() {
                while(cleanerOn){
                    try {
                        Thread.sleep(Constraints.LOG_CACHE_EXPIRE_TIME_IN_SECONDS.getValue());
                        //Just invoke the auto cleaner
                        cacheHolder.get("log", () -> null);
                    } catch (Exception e){
                        //Ignore
                    }
                }
            }
        };
        scheduler.submit(cleaner);
    }

    @PreDestroy
    public void destroy(){
        this.cleanerOn = false;
        if (Objects.nonNull(this.cleaner.future())){
            this.cleaner.future().cancel(true);
        }
    }

    @Override
    public LogResult logsFromPage( String jobExecId, LogQuery logQuery) {
        LaunchedExchangisJobEntity launchedExchangisJob = launchedJobDao.searchLogPathInfo(jobExecId);
        return logsFromPageAndPath(launchedExchangisJob.getLogPath(), logQuery);
    }

    @Override
    public void appendLog(String tenancy, String jobExecId, List<String> logs) {
        appendLog(jobExecId, logs);
    }

    @Override
    public void appendLog(String jobExecId, List<String> logs) {
        JobLogCache<String> cache = getOrCreateLogCache(jobExecId);
        logs.forEach(cache ::cacheLog);
    }


    @Override
    public JobLogCache<String> getOrCreateLogCache(String jobExecId){
        try {
            return cacheHolder.get(jobExecId, () -> {
                LaunchedExchangisJobEntity launchedExchangisJob = launchedJobDao.searchLogPathInfo(jobExecId);
                if (Objects.nonNull(launchedExchangisJob)) {
                    return loadJobLogCache(jobExecId, launchedExchangisJob);
                }
                return null;
            });
        } catch (ExecutionException e) {
            throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(),"Fail to create the job log cache of [" + jobExecId +"]", e);
        }
    }

    /**
     * Load job log cache
     * @param launchedExchangisJob job
     * @return log cache
     */
    protected abstract AbstractJobLogCache<String> loadJobLogCache(String jobExcId, LaunchedExchangisJobEntity launchedExchangisJob)
        throws Exception;
}
