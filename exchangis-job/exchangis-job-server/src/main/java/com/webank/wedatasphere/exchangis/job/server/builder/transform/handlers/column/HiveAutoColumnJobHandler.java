package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;



/**
 * Hive auto column handler
 */
public class HiveAutoColumnJobHandler extends AutoColumnSubExchangisJobHandler {


    @Override
    public String dataSourceType() {
        return "hive";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    protected boolean autoColumn() {
        return true;
    }

    /**
     * Resume the params in thread local
     * @return bool
     */
    @Override
    protected boolean resumeParams() {
        return true;
    }
}
