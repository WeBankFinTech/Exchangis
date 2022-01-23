package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisEvent;

public class TaskExecutionEvent implements ExchangisEvent {
    private long eventTime;

    private LaunchedExchangisTask launchedExchangisTask;
    public TaskExecutionEvent(LaunchedExchangisTask task){
        this.eventTime = System.currentTimeMillis();
        this.launchedExchangisTask = task;
    }
    @Override
    public String eventId() {
        return "_TaskExecution_" + launchedExchangisTask.getTaskId();
    }

    @Override
    public void setEventId(String eventId) {
        //null
    }

    public LaunchedExchangisTask getLaunchedExchangisTask() {
        return launchedExchangisTask;
    }

    @Override
    public long getEventTime() {
        return this.eventTime;
    }

    @Override
    public void setEventTime(long timestamp) {
        this.eventTime = timestamp;
    }
}
