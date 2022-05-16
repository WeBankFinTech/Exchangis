package com.webank.wedatasphere.exchangis.job.server.log;

import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public interface JobServerLogging<T> {
    default Logger getLogger(){
        return LoggerFactory.getLogger(this.getClass());
    }

    default void trace(T entity, String message, Object... args) {
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isTraceEnabled()){
            logger.trace(message, args);
        }
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.INFO, entity, message, args)));
    }

    default void debug(T entity, String message){
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isDebugEnabled()){
            logger.debug(message);
        }
    }

    default void info(T entity, String message, Object... args){
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isInfoEnabled()){
            logger.info(message, args);
        }
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.INFO, entity, message, args)));
    }

    default void info(T entity, String message, Throwable t){
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isInfoEnabled()){
            logger.info(message, t);
        }
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.INFO, entity, message, t)));
    }

    default void warn(T entity, String message, Object... args){
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isWarnEnabled()){
            logger.warn(message, args);
        }
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.WARN, entity, message, args)));
    }

    default void warn(T entity, String message, Throwable t){
        Logger logger = getLogger();
        if (Objects.nonNull(logger) && logger.isWarnEnabled()){
            logger.warn(message, t);
        }
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.WARN, entity, message, t)));
    }

    default void error(T entity, String message, Object... args){
        Optional.ofNullable(getLogger()).ifPresent(logger -> logger.error(message, args));
        Optional.ofNullable(getJobLogListener()).ifPresent(listener ->
                listener.onAsyncEvent(getJobLogEvent(JobLogEvent.Level.ERROR, entity, message, args)));
    }

    default JobLogListener getJobLogListener() {
        return null;
    }

    /**
     * Job log event
     * @param level level
     * @param entity entity
     * @param message message
     * @param args args
     * @return
     */
    default JobLogEvent getJobLogEvent(JobLogEvent.Level level, T entity, String message, Object... args){
        return new JobLogEvent(level, null, null, message, args);
    }
}
