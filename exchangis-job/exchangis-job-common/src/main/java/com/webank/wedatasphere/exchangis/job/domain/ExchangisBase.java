package com.webank.wedatasphere.exchangis.job.domain;

import java.util.Date;

/**
 * Basic method of entity
 */
public interface ExchangisBase {

    /**
     * ID
     * @return LONG
     */
    Long getId();

    void setId(Long id);


    /**
     * Name
     * @return STRING
     */
    String getName();

    void setName(String name);

    /**
     * Create time
     * @return DATE
     */
    Date getCreateTime();

    void setCreateTime(Date createTime);

    /**
     * Last update time
     * @return DATE
     */
    Date getLastUpdateTime();

    void setLastUpdateTime(Date lastUpdateTime);
}
