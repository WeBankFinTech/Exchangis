package com.webank.wedatasphere.exchangis.job.log;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Query task Log
 */
public class LogQuery {

    private int fromLine = 1;

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
    public LogQuery(int fromLine, int pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows){
        this.fromLine = fromLine;
        this.pageSize = pageSize;
        this.ignoreKeywords = ignoreKeywords;
        this.onlyKeywords = onlyKeywords;
        this.lastRows = lastRows;
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

    public List<String> getIgnoreKeywordsList(){
        if (StringUtils.isNotBlank(this.ignoreKeywords)){
            return Arrays.asList(this.ignoreKeywords.split(","));
        }
        return Collections.emptyList();
    }
    public void setIgnoreKeywords(String ignoreKeywords) {
        this.ignoreKeywords = ignoreKeywords;
    }

    public String getOnlyKeywords() {
        return onlyKeywords;
    }

    public List<String> getOnlyKeywordsList(){
        if (StringUtils.isNotBlank(this.onlyKeywords)){
            return Arrays.asList(this.onlyKeywords.split(","));
        }
        return Collections.emptyList();
    }

    public void setOnlyKeywords(String onlyKeywords) {
        this.onlyKeywords = onlyKeywords;
    }
}
