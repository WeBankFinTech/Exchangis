package com.webank.wedatasphere.exchangis.datasource.core.splitter;

import java.util.List;
import java.util.Map;

/**
 * Data source splitter
 */
public interface DataSourceSplitStrategy {
    /**
     * Splitter name
     * @return name
     */
    String name();

    /**
     * Get split values
     * @param dataSourceParams data source params
     * @return value list
     */
    List<Map<String, Object>> getSplitValues(Map<String, Object> dataSourceParams, String[] splitKeys);
}
