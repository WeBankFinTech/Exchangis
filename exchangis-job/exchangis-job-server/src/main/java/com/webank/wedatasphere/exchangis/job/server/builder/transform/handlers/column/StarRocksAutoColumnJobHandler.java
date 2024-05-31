package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

/**
 * StarRocks auto column handler
 */
public class StarRocksAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler{

    @Override
    public String dataSourceType() {
        return "starrocks";
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
