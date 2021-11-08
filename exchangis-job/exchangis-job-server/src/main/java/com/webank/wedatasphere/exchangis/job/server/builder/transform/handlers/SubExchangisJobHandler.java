package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * JobHandler
 */
public interface SubExchangisJobHandler {

    /**
     * Accepted data source type
     * @return string
     */
    String dataSourceType();

    /**
     * Handle source
     * @param subExchangisJob sub job
     * @param ctx context
     */
    void handleSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException;


    /**
     * Handle sink
     * @param subExchangisJob
     * @param ctx
     */
    void handleSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException;
}
