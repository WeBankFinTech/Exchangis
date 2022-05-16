package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import org.apache.linkis.common.exception.ErrorException;

/**
 * JobHandler
 */
public interface SubExchangisJobHandler {

    String DEFAULT_DATA_SOURCE_TYPE = "default";
    /**
     * Associated data source type
     * @return string
     */
    String dataSourceType();

    /**
     * If accept engine type
     * @param engineType engine type
     * @return boolean
     */
    default boolean acceptEngine(String engineType){
        return true;
    }
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

    /**
     * Order
     * @return value
     */
    default int order(){
        return Integer.MAX_VALUE;
    }
}
