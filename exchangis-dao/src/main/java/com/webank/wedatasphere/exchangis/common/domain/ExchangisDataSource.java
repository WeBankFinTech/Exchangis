package com.webank.wedatasphere.exchangis.common.domain;

/**
 * Data source common basic inf
 */
public interface ExchangisDataSource {

    /**
     * Id
     * @return id
     */
    Long getId();

    void setId(Long id);

    /**
     * Data source type
     * @return type
     */
    String getType();

    void setType(String type);
    /**
     * Data source name
     * @return name
     */
    String getName();

    void setName(String name);

    /**
     * Create user or owner
     * @return username
     */
    String getCreator();

    void setCreator(String creator);

    /**
     * Data source description
     * @return desc
     */
    String getDesc();

    void setDesc(String desc);
}
