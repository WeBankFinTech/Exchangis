package com.webank.wedatasphere.exchangis.job.server.execution.generator.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisEvent;

public class TaskGenerateEvent implements ExchangisEvent {

    private long eventTime;

    private LaunchableExchangisJob launchableExchangisJob;

    public TaskGenerateEvent(long eventTime, LaunchableExchangisJob launchableExchangisJob){
        this.eventTime = eventTime;
        this.launchableExchangisJob = launchableExchangisJob;
    }

    @Override
    public String eventId() {
        return "_TaskGenerate_" + launchableExchangisJob.getJobExecutionId();
    }

    @Override
    public void setEventId(String eventId) {
        //null
    }

    @Override
    public long getEventTime() {
        return this.eventTime;
    }

    @Override
    public void setEventTime(long timestamp) {
        this.eventTime = timestamp;
    }

    public LaunchableExchangisJob getLaunchableExchangisJob() {
        return launchableExchangisJob;
    }

    public void setLaunchableExchangisJob(LaunchableExchangisJob launchableExchangisJob) {
        this.launchableExchangisJob = launchableExchangisJob;
    }
}
