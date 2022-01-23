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
        getLogger().info("Event: [id: {}, type: {}] in listener [{}]", event.eventId(), event.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (event instanceof TaskMetricsUpdateEvent){
            onMetricsUpdate((TaskMetricsUpdateEvent)event);
        } else if (event instanceof TaskStatusUpdateEvent){
            onStatusUpdate((TaskStatusUpdateEvent)event);
        } else if (event instanceof TaskInfoUpdateEvent){
            onInfoUpdate((TaskInfoUpdateEvent)event);
        } else if (event instanceof TaskDeleteEvent){
            onDelete((TaskDeleteEvent)event);
        }
    }

    /**
     * Listen metrics update
     * @param metricsUpdateEvent update event
     */
    void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent);

    /**
     * Status update
     * @param statusUpdateEvent update event
     */
    void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent);

    /**
     * Info update
     * @param infoUpdateEvent update event
     */
    void onInfoUpdate(TaskInfoUpdateEvent infoUpdateEvent);

    /**
     * Delete
     * @param deleteEvent delete event
     */
    void onDelete(TaskDeleteEvent deleteEvent);

    /**
     * Progress update
     * @param updateEvent update event
     */
    void onProgressUpdate(TaskProgressUpdateEvent updateEvent);
}
