package com.webank.wedatasphere.exchangis.job.domain;


import java.util.Map;

/**
 * Basic job interface
 */
public interface ExchangisJob extends ExchangisBase{

    /**
     * Engine type
     * @return type value
     */
    String getEngineType();

    void setEngineType(String engineType);

    /**
     * Label String
     * @return Label value
     */
    String getJobLabel();

    void setJobLabel(String engineType);

    /**
     * Label map
     * @return entities
     */
    Map<String, Object> getJobLabels();

    /**
     * Set label Json
     * @param labels
     */
    void setJobLabels(String labels);

    /**
     * Set label Map
     * @param jobLabels
     */
    void setJobLabels(Map<String, Object> jobLabels);
    /**
     * Create user
     * @return user name
     */
    String getCreateUser();

    void setCreateUser(String createUser);
}
