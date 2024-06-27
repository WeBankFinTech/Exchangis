package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

/**
 * Mongo auto column handler
 */
public class MongoAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler{

    @Override
    public String dataSourceType() {
        return "mongodb";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    protected boolean autoColumn() {
        return true;
    }
}
