package com.webank.wedatasphere.exchangis.job.server.log.rpc;

import org.apache.linkis.protocol.message.RequestProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Send log request
 */
public class SendLogRequest implements RequestProtocol {
    /**
     * Exec id
     */
    private String jobExecId;

    /**
     * Is reached the end of log
     */
    private boolean isEnd;
    /**
     * Log lines
     */
    private List<String> logLines = new ArrayList<>();

    public SendLogRequest(String jobExecId,
                          boolean isEnd,
                          List<String> logLines){
        this.jobExecId = jobExecId;
        this.isEnd = isEnd;
        this.logLines = logLines;
    }

    public String getJobExecId() {
        return jobExecId;
    }

    public void setJobExecId(String jobExecId) {
        this.jobExecId = jobExecId;
    }

    public List<String> getLogLines() {
        return logLines;
    }

    public void setLogLines(List<String> logLines) {
        this.logLines = logLines;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
