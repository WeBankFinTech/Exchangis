package com.webank.wedatasphere.exchangis.job.listener;


/**
 * Event between different modules
 */
public interface ExchangisEvent {

    String eventId();

    void setEventId(String eventId);

    long getEventTime();

    void setEventTime(long timestamp);

}
