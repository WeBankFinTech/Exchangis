package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

/**
 * ES auto column handler
 */
public class EsAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler{
    @Override
    public String dataSourceType() {
        return "elasticsearch";
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
