package com.webank.wedatasphere.exchangis.job.server.log.rpc;

import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import org.apache.linkis.protocol.message.RequestProtocol;

/**
 * Fetch log request
 */
public class FetchLogRequest extends LogQuery implements RequestProtocol {

    /**
     * Log path
     */
    private String logPath;

    public FetchLogRequest(LogQuery logQuery, String logPath){
        super(logQuery.getFromLine(), logQuery.getPageSize(),
                logQuery.getIgnoreKeywords(), logQuery.getOnlyKeywords(),
                logQuery.getLastRows());
        setEnableTail(logQuery.isEnableTail());
        this.logPath = logPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
