package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.listener.ExchangisListener;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;

/**
 * Execution listener
 */
public interface TaskExecutionListener extends ExchangisListener<TaskExecutionEvent> {
    /**
     * Listen event during task execution
     * @param event event
     */
    default void onEvent(TaskExecutionEvent event) throws ExchangisOnEventException{
        getLogger().trace("Event: [id: {}, type: {}] in listener [{}]", event.eventId(), event.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (event instanceof TaskMetricsUpdateEvent){
            onMetricsUpdate((TaskMetricsUpdateEvent)event);
        } else if (event instanceof TaskStatusUpdateEvent){
            onStatusUpdate((TaskStatusUpdateEvent)event);
        } else if (event instanceof TaskLaunchEvent){
            onLaunch((TaskLaunchEvent)event);
        } else if (event instanceof TaskDeleteEvent){
            onDelete((TaskDeleteEvent)event);
        } else if (event instanceof TaskProgressUpdateEvent){
            onProgressUpdate((TaskProgressUpdateEvent)event);
        } else if (event instanceof TaskDequeueEvent){
            onDequeue((TaskDequeueEvent) event);
        }
    }

    /**
     * Listen metrics update
     * @param metricsUpdateEvent update event
     */
    void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent) throws ExchangisOnEventException;

    /**
     * Status update
     * @param statusUpdateEvent update event
     */
    void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent) throws ExchangisOnEventException;

    /**
     * Info update
     * @param infoUpdateEvent update event
     */
    void onLaunch(TaskLaunchEvent infoUpdateEvent) throws ExchangisOnEventException;

    /**
     * Delete
     * @param deleteEvent delete event
     */
    void onDelete(TaskDeleteEvent deleteEvent) throws ExchangisOnEventException;

    /**
     * Dequeue event
     * @param dequeueEvent dequeue event
     * @throws ExchangisOnEventException exception
     */
    void onDequeue(TaskDequeueEvent dequeueEvent) throws ExchangisOnEventException;

    /**
     * Progress update
     * @param updateEvent update event
     */
    void onProgressUpdate(TaskProgressUpdateEvent updateEvent) throws ExchangisOnEventException;

}
