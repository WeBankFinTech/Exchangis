package com.webank.wedatasphere.exchangis.job.log;

import java.util.ArrayList;
import java.util.List;

/**
 * Task log
 */
public class LogResult {

    /**
     * End line
     */
    private int endLine;

    /**
     * If is end
     */
    private boolean isEnd;

    /**
     * Log content
     */
    private List<String> logs =  new ArrayList<>();

    public LogResult(int endLine, boolean isEnd, List<String> logs){
        this.endLine = endLine;
        this.isEnd = isEnd;
        this.logs = logs;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
