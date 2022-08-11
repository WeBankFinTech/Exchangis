package com.webank.wedatasphere.exchangis.job.server.log.service;

import com.google.common.cache.*;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.log.cache.AbstractJobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.scheduler.Scheduler;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR;

/**
 * Just store the log into the local
 */
public class LocalSimpleJobLogService implements JobLogService {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSimpleJobLogService.class);

    private Cache<String, JobLogCache<String>> cacheHolder;

    private AbstractExchangisSchedulerTask cleaner;

    private volatile boolean cleanerOn;

    private static class Constraints{
        public static final CommonVars<String> LOG_LOCAL_PATH = CommonVars.apply("wds.exchangis.job.log.local.path", "/data/bdp/dss/exchangis/main/logs");

        public static final CommonVars<Integer> lOG_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.log.cache.size", 15);

        public static final CommonVars<Integer> LOG_CACHE_EXPIRE_TIME_IN_SECONDS = CommonVars.apply("wds.exchangis.job.log.cache.expire.time-in-seconds", 5);

        public static final CommonVars<String> LOG_MULTILINE_PATTERN = CommonVars.apply("wds.exchangis.log.multiline.pattern", "^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
    }

    @Resource
    private Scheduler scheduler;

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
    public LogResult logsFromPageAndPath(String logPath, LogQuery logQuery) {
        String fullPath = Constraints.LOG_LOCAL_PATH.getValue() + IOUtils.DIR_SEPARATOR_UNIX + logPath;
        LogResult result = new LogResult(0, false, Collections.emptyList());
        if (!new File(fullPath).exists()){
            return result;
        }
        if (logQuery.getLastRows() != null && logQuery.getLastRows() > 0){
            return getLastRows(fullPath, logQuery.getLastRows());
        }
        RandomAccessFile logReader = null;
        try {
            logReader = new RandomAccessFile(fullPath, "rw");
            String patternValue = Constraints.LOG_MULTILINE_PATTERN.getValue();
            Pattern linePattern = StringUtils.isNotBlank(patternValue)? Pattern.compile(patternValue) : null;
            int readLine = 0;
            int lineNum = 0;
            int skippedLine = 0;
            int ignoreLine = 0;
            int pageSize = logQuery.getPageSize();
            int fromLine = logQuery.getFromLine();
            List<String> ignoreKeywords = logQuery.getIgnoreKeywordsList();
            List<String> onlyKeywords = logQuery.getOnlyKeywordsList();
            boolean rowIgnore = false;
            String line = logReader.readLine();
            List<String> logs = new ArrayList<>();
            while (readLine < pageSize && line != null){
                lineNum += 1;
                if (skippedLine < fromLine - 1){
                    skippedLine += 1;
                } else {
                    if (rowIgnore) {
                        if (Objects.nonNull(linePattern)){
                            Matcher matcher = linePattern.matcher(line);
                            if (matcher.find()){
                                ignoreLine = 0;
                                rowIgnore = !isIncludeLine(line, onlyKeywords, ignoreKeywords);
                            } else {
                                ignoreLine += 1;
                                // TODO limit the value of ignoreLine
                            }
                        }else{
                            rowIgnore = !isIncludeLine(line, onlyKeywords, ignoreKeywords);
                        }
                    }else {
                        rowIgnore = !isIncludeLine(line, onlyKeywords, ignoreKeywords);
                    }
                    if (!rowIgnore) {
                        if (line.contains("password")) {
                            LOG.info("have error information");
                        }
                        if (!line.contains("password")) {
                            logs.add(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                        }
                        readLine += 1;
                    }
                }
                line = logReader.readLine();
            }
            result = new LogResult(lineNum, false, logs);
        } catch (IOException e) {
            throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(),"Unable to query the logs from path: [" + logPath + "]", e);
        } finally {
            if (Objects.nonNull(logReader)) {
                try {
                    logReader.close();
                } catch (IOException e) {
                    //Ignore
                }
            }
        }
        return result;
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


    private boolean isIncludeLine(String line, List<String> onlyKeywordList, List<String> ignoreKeywordList){
        boolean accept = ignoreKeywordList.isEmpty() || ignoreKeywordList.stream().noneMatch(line::contains);
        if (accept){
            accept = onlyKeywordList.isEmpty() || onlyKeywordList.stream().anyMatch(line::contains);
        }
        return accept;
    }

    /**
     * Get last rows
     * @param fullPath full path
     * @param lastRows last rows
     * @return
     */
    private LogResult getLastRows(String fullPath, int lastRows){
        try {
            List<String> logs = Arrays.asList(Utils.exec(new String[]{"tail", "-n", lastRows + "", fullPath}, 5000L).split("\n"));
            return new LogResult(0, true, logs);
        }catch (Exception e){
            throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(), "Fail to get last rows from path: [" + fullPath + "]", e);
        }
    }
    @Override
    public JobLogCache<String> getOrCreateLogCache(String jobExecId){
        try {
            return cacheHolder.get(jobExecId, () -> {
                LaunchedExchangisJobEntity launchedExchangisJob = launchedJobDao.searchLogPathInfo(jobExecId);
                if (Objects.nonNull(launchedExchangisJob)) {
                    File logFile = new File(Constraints.LOG_LOCAL_PATH.getValue() + IOUtils.DIR_SEPARATOR_UNIX +
                            launchedExchangisJob.getLogPath());
                    if (!logFile.exists()){
                        // Write empty string to create new file
                        FileUtils.writeStringToFile(logFile, "");
                        LOG.info("Create the new job log file: {}", logFile.getAbsolutePath());
                    }
                    RandomAccessFile file = new RandomAccessFile(logFile, "rw");
                    // Seek to the end of file
                    file.seek(file.length());
                    return new AbstractJobLogCache<String>(scheduler, 100, 2000) {
                        @Override
                        public synchronized void flushCache(boolean isEnd) {
                            // Store into local path
                            if (!cacheQueue().isEmpty()) {
                                try {
                                    List<Object> logLines = new ArrayList<>();
                                    cacheQueue().drainTo(logLines);
                                    for (Object line : logLines) {
                                        file.write(String.valueOf(line).getBytes(Charset.defaultCharset()));
                                    }
                                } catch (IOException ex) {
                                    LOG.error("Fail to flush the log cache of [" + launchedExchangisJob.getJobExecutionId() + "]", ex);
                                }
                            }
                            if (isEnd) {
                                cacheHolder.invalidate(jobExecId);
                                try {
                                    file.close();
                                } catch (IOException e) {
                                    //Ignore
                                }
                            }
                        }
                    };
                }
                return null;
            });
        } catch (ExecutionException e) {
            throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(),"Fail to create the job log cache of [" + jobExecId +"]", e);
        }
    }

}
