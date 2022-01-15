package com.webank.wedatasphere.exchangis.job.server.vo;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/1/12 22:43
 */
public class ExchangisJobAndTaskStatusVO {
    private String status;

    private Long progress;

    private Map<String, Object> tasks;

    public ExchangisJobAndTaskStatusVO(String status, Long progress, Map<String, Object> tasks) {
        this.status = status;
        this.progress = progress;
        this.tasks = tasks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    public Map<String, Object> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, Object> tasks) {
        this.tasks = tasks;
    }
}
