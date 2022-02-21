package com.webank.wedatasphere.exchangis.job.server.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * Category log
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangisCategoryLogVo {

    @JsonIgnore
    private Map<String, CategoryLog> categoryStrategy = new HashMap<>();

    private int endLine;

    private boolean isEnd = false;

    private Map<String, String> logs = new HashMap<>();

    public void newCategory(String name, Function<String, Boolean> logAcceptable){
        this.categoryStrategy.put(name, new CategoryLog(logAcceptable));
    }

    public void processLogResult(LogResult logResult, boolean acceptEmpty){
        this.endLine = logResult.getEndLine();
        this.isEnd = logResult.isEnd();
        List<CategoryLog> categoryLogs = new ArrayList<>(this.categoryStrategy.values());
        Optional.ofNullable(logResult.getLogs()).ifPresent(logs -> logs.forEach(log -> {
            for (CategoryLog categoryLog : categoryLogs){
                if (categoryLog.logAcceptable.apply(log)) {
                    categoryLog.logs.add(log);
                }
            }
        }));
        this.categoryStrategy.forEach((category, categoryLog) -> {
            if (!categoryLog.logs.isEmpty() || acceptEmpty) {
                this.logs.put(category, StringUtils.join(categoryLog.logs, "\n"));
                categoryLog.logs.clear();
            }
        });
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(boolean end) {
        isEnd = end;
    }

    public Map<String, String> getLogs() {
        return logs;
    }

    public void setLogs(Map<String, String> logs) {
        this.logs = logs;
    }

    private static class CategoryLog{

        private List<String> logs = new ArrayList<>();

        private Function<String, Boolean> logAcceptable;

        public CategoryLog(Function<String, Boolean> logAcceptable){
            this.logAcceptable = logAcceptable;
        }
    }
}
