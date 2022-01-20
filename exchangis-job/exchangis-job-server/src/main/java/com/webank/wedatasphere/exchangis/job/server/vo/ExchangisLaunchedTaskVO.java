package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskMetricsDTO;

/**
 * @author tikazhang
 * @Date 2022/1/12 22:50
 */
public class ExchangisLaunchedTaskVO {

    private Long taskId;

    private String name;

    private String status;

    private Long progress;

    public ExchangisLaunchedTaskVO(Long taskId, String name, String status, Long progress){
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.progress = progress;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
