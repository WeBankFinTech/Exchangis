package com.webank.wedatasphere.exchangis.job.listener.events;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisEvent;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

public class JobLogEvent implements ExchangisEvent {

    public enum Level{
        TRACE, INFO, WARN, ERROR
    }
    private String jobExecutionId;

    private String message;

    private Level level = Level.INFO;

    private Object[] args;

    private Throwable throwable;

    private long eventTime;

    private String tenancy = "default";

    public JobLogEvent(Level level, String tenancy, String jobExecutionId, String message, Object... args){
        if (StringUtils.isNotBlank(tenancy)){
            this.tenancy = tenancy;
        }
        this.jobExecutionId = jobExecutionId;
        this.message = message;
        this.eventTime = System.currentTimeMillis();
        if (Objects.nonNull(level)){
            this.level = level;
        }
        if (args != null && args.length > 0){
            Object lastEntry = args[args.length - 1];
            if (lastEntry instanceof Throwable ){
                this.throwable = (Throwable)lastEntry;
                this.level = Level.ERROR;
            }
            this.args = args;
        }
    }

    public JobLogEvent(String tenancy, String jobExecutionId, String message, Object... args){
        this(Level.INFO, tenancy, jobExecutionId, jobExecutionId, message, args);
    }
    public JobLogEvent(String jobExecutionId, String message, Object... args){
        this(Level.INFO, null, jobExecutionId, message, args);
    }
    @Override
    public String eventId() {
        return jobExecutionId;
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

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getTenancy() {
        return tenancy;
    }

    public Level getLevel() {
        return level;
    }
}
