package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

/**
 * Oracle auto column handler
 */
public class OracleAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler{
    @Override
    public String dataSourceType() {
        return "oracle";
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
