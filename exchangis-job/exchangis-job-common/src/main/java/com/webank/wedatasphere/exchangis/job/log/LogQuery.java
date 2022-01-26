package com.webank.wedatasphere.exchangis.job.log;

/**
 * Query task Log
 */
public class LogQuery {

    private int fromLine = 0;

    private int pageSize = 100;

    private String ignoreKeywords;

    private String onlyKeywords;

    private Integer lastRows;

    public LogQuery(){

    }

    public LogQuery(int fromLine, int pageSize){
        this.fromLine = fromLine;
        this.pageSize = pageSize;
    }

    public Integer getLastRows() {
        return lastRows;
    }

    public void setLastRows(Integer lastRows) {
        this.lastRows = lastRows;
    }

    public int getFromLine() {
        return fromLine;
    }

    public void setFromLine(int fromLine) {
        this.fromLine = fromLine;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getIgnoreKeywords() {
        return ignoreKeywords;
    }

    public void setIgnoreKeywords(String ignoreKeywords) {
        this.ignoreKeywords = ignoreKeywords;
    }

    public String getOnlyKeywords() {
        return onlyKeywords;
    }

    public void setOnlyKeywords(String onlyKeywords) {
        this.onlyKeywords = onlyKeywords;
    }
}
