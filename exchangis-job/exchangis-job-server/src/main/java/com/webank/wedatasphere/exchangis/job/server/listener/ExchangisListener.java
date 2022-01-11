package com.webank.wedatasphere.exchangis.job.server.listener;

import org.apache.linkis.common.exception.ErrorException;

/**
 * Listener
 */
public interface ExchangisListener {

    /**
     * Subscribe the event
     * @param event
     */
    default void onEvent(ExchangisEvent event) throws ErrorException {

    }
}
