package com.webank.wedatasphere.exchangis.job.server.log.service;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.log.cache.AbstractJobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache;
import com.webank.wedatasphere.exchangis.job.server.log.rpc.FetchLogResponse;
import com.webank.wedatasphere.exchangis.job.server.log.rpc.SendLogRequest;
import com.webank.wedatasphere.exchangis.job.server.log.rpc.FetchLogRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.ServiceInstance;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.rpc.message.annotation.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR;

/**
 * Rpc job log service
 */
public class RpcJobLogService extends AbstractJobLogService{

    private static final Logger LOG = LoggerFactory.getLogger(RpcJobLogService.class);


    @Receiver
    public void appendLog(SendLogRequest sendLogRequest){
        String jobExecId = sendLogRequest.getJobExecId();
        List<String> logLines = sendLogRequest.getLogLines();
        if (logLines.size() > 0) {
            // Two level cache
            JobLogCache<String> cache = getOrCreateLogCache(jobExecId);
            logLines.forEach(cache :: cacheLog);
            if (sendLogRequest.isEnd()){
                cache.flushCache(true);
            }
        } else if (sendLogRequest.isEnd()){
            Optional.ofNullable(cacheHolder.getIfPresent(jobExecId)).ifPresent( cache -> {
                cache.flushCache(true);
            });
        }
    }

    @Receiver
    public FetchLogResponse logsFromPage(FetchLogRequest fetchLogRequest){
        return new FetchLogResponse(
                logsFromPageAndPath(fetchLogRequest.getLogPath(), fetchLogRequest));
    }
    @Override
    protected AbstractJobLogCache<String> loadJobLogCache(String jobExecId,
                                                          LaunchedExchangisJobEntity launchedExchangisJob) throws Exception{
        String logPath = launchedExchangisJob.getLogPath();
        int splitPos = logPath.indexOf("@");
        if (splitPos > 0){
            String logAddress = logPath.substring(0, splitPos);
            if (!logAddress.equals(EnvironmentUtils.getServerAddress())){
                ServiceInstance instance = ServiceInstance.apply(EnvironmentUtils.getServerName(), logAddress);
                return new AbstractJobLogCache<String>(scheduler, 100, 2000) {
                    @Override
                    public void flushCache(boolean isEnd) {
                        // Send rpc
                        if (!cacheQueue().isEmpty()) {
                            try {
                                List<String> logLines = new ArrayList<>();
                                cacheQueue().drainTo(logLines);
                                Sender.getSender(instance).send(new SendLogRequest(jobExecId, isEnd, logLines));
                            } catch (Exception ex) {
                                LOG.error("Fail to send the log cache of [" + launchedExchangisJob.getJobExecutionId()
                                        + "] to remote rpc [" + logAddress + "]", ex);
                            }
                        }
                        if (isEnd) {
                            cacheHolder.invalidate(jobExecId);
                        }
                    }
                };
            }
            logPath = logPath.substring(splitPos + 1);
        }
        File logFile = new File(Constraints.LOG_LOCAL_PATH.getValue() + IOUtils.DIR_SEPARATOR_UNIX +
                logPath);

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

    @Override
    public LogResult logsFromPageAndPath(String logPath, LogQuery logQuery) {
        LogResult result = new LogResult(0, false, Collections.emptyList());
        int splitPos = logPath.indexOf("@");
        if (splitPos > 0) {
            String logAddress = logPath.substring(0, splitPos);
            if (!logAddress.equals(EnvironmentUtils.getServerAddress())) {
                Object response;
                try {
                    response = Sender.getSender(ServiceInstance.apply(EnvironmentUtils.getServerName(), logAddress))
                            .ask(new FetchLogRequest(logQuery, logPath));
                } catch (Exception e){
                    LOG.warn("Rpc request exception in fetching log from: [" + logPath + "]", e);
                    // Just end the log request
                    result.setEnd(true);
                    return result;
                }
                if (response instanceof FetchLogResponse){
                    return (LogResult) response;
                }
                throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(),"Unable to fetch log from: [" + logPath +
                        "], unknown request protocol: [" + response + "]", null);
            }
            logPath = logPath.substring(splitPos + 1);
        }
        String fullPath = Constraints.LOG_LOCAL_PATH.getValue() + IOUtils.DIR_SEPARATOR_UNIX + logPath;
        if (!new File(fullPath).exists()){
            return result;
        }
        if (logQuery.getLastRows() != null && logQuery.getLastRows() > 0){
            return getLastRows(fullPath, logQuery.getLastRows());
        }
        RandomAccessFile logReader = null;
        ReversedLinesFileReader reverseReader = null;
        try {
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
            Supplier<String> lineSupplier = null;
            if (logQuery.isEnableTail()){
                reverseReader = new ReversedLinesFileReader(new File(fullPath), Charset.defaultCharset());
                LOG.trace("Enable reverse read the log: {}, fromLine: {}, pageSize: {}", fullPath, fromLine, pageSize);
                ReversedLinesFileReader finalReverseReader = reverseReader;
                lineSupplier = () -> {
                    try {
                        return finalReverseReader.readLine();
                    } catch (IOException e) {
                        throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(), e.getMessage(), e);
                    }
                };
            } else {
                logReader = new RandomAccessFile(fullPath, "rw");
                RandomAccessFile finalLogReader = logReader;
                lineSupplier = () -> {
                    try {
                        String line =  finalLogReader.readLine();
                        if (null != line){
                            return new String(line.getBytes(StandardCharsets.ISO_8859_1), Charset.defaultCharset());
                        }
                        return null;
                    } catch (IOException e) {
                        throw new ExchangisJobServerException.Runtime(LOG_OP_ERROR.getCode(), e.getMessage(), e);
                    }
                };
            }
            String line = lineSupplier.get();
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
                        if (!line.contains("password")) {
                            logs.add(line);
                        }
                        readLine += 1;
                    }
                }
                line = lineSupplier.get();
            }
            if (logQuery.isEnableTail()){
                Collections.reverse(logs);
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
            if (Objects.nonNull(reverseReader)) {
                try {
                    reverseReader.close();
                } catch (IOException e) {
                    //Ignore
                }
            }
        }
        return result;
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

    private boolean isIncludeLine(String line, List<String> onlyKeywordList, List<String> ignoreKeywordList){
        boolean accept = ignoreKeywordList.isEmpty() || ignoreKeywordList.stream().noneMatch(line::contains);
        if (accept){
            accept = onlyKeywordList.isEmpty() || onlyKeywordList.stream().anyMatch(line::contains);
        }
        return accept;
    }
}
