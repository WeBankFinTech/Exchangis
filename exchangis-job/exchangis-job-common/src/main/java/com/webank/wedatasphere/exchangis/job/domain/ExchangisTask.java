package com.webank.wedatasphere.exchangis.job.domain;

/**
 * Basic task interface
 */
public interface ExchangisTask extends ExchangisBase {

    /**
     * Engine type
     * @return type value
     */
    String getEngineType();

    void setEngineType(String engineType);

    /**
     * Execute user
     * @return
     */
    String getExecuteUser();

    void setExecuteUser(String executeUser);

    /**
     * Related server instance
     * @return
     */
    String getInstance();

    void setInstance(String instance);
}
