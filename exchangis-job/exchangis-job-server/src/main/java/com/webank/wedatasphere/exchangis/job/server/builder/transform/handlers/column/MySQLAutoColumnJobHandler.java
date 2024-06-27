package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

/**
 * Mysql auto column handler
 */
public class MySQLAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler {
    @Override
    protected boolean autoColumn() {
        return false;
    }

    @Override
    public String dataSourceType() {
        return "mysql";
    }


    @Override
    public int order() {
        return 0;
    }
}
