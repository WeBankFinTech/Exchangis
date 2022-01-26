package com.webank.wedatasphere.exchangis.job.server.log.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.log.cache.AbstractJobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.serivce.DefaultJobLogService;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.jvnet.hk2.annotations.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Just store the log into the local
 */
@Service
public class LocalSimpleJobLogService implements JobLogService {

    private LoadingCache<String, JobLogCache<String>> cacheHolder;

    private AbstractExchangisSchedulerTask cleaner;

    private volatile boolean cleanerOn;

    private static class Constraints{
        public static final CommonVars<String> LOG_LOCAL_PATH = CommonVars.apply("wds.exchangis.job.log.local.path", "/data/bdp/exchangis/logs");

        public static final CommonVars<Integer> lOG_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.log.cache.size", 15);

        public static final CommonVars<Integer> LOG_CACHE_ALIVE_TIME_IN_SECONDS = CommonVars.apply("wds.exchangis.job.log.cache.alive.time-in-seconds", 5);
    }

    @Resource
    private Scheduler scheduler;

    @PostConstruct
    public void init(){
        cleanerOn = true;
        cacheHolder = CacheBuilder.newBuilder().maximumSize(Constraints.lOG_CACHE_SIZE.getValue())
                .expireAfterAccess(Constraints.LOG_CACHE_ALIVE_TIME_IN_SECONDS.getValue(), TimeUnit.SECONDS)
                .build(new CacheLoader<String, JobLogCache<String>>() {
                    @Override
                    public JobLogCache<String> load(String jobExecutionId) throws Exception {
                        return null;
                    }
                });
        cleaner = new AbstractExchangisSchedulerTask() {
            @Override
            public String getTenancy() {
                return "log";
            }

            @Override
            public String getName() {
                return "Job-Log-Cache-Cleaner";
            }

            @Override
            public JobInfo getJobInfo() {
                return null;
            }

            @Override
            protected void schedule() {
                while(cleanerOn){
                    try {
                        Thread.sleep(Constraints.LOG_CACHE_ALIVE_TIME_IN_SECONDS.getValue());
                        cacheHolder.cleanUp();
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
        return null;
    }

    @Override
    public void appendLog(String tenancy, String jobExecId, List<String> logs) {

    }

    @Override
    public void appendLog(String jobExecId, List<String> logs) {

    }


    @Override
    public JobLogCache<String> getOrCreateLogCache(String jobExecId) {
        try {
            return cacheHolder.get(jobExecId);
        } catch (ExecutionException e) {
            //TODO Log
        }
        return null;
    }

}
