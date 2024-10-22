package com.webank.wedatasphere.exchangis.datasource.core.splitter;

import java.util.List;
import java.util.Map;

/**
 * Split strategy: field split
 */
public class DataSourceFieldSplitStrategy implements DataSourceSplitStrategy{
    @Override
    public String name() {
        return null;
    }

    @Override
    public List<Map<String, Object>> getSplitValues(Map<String, Object> dataSourceParams, String[] splitKeys) {

        return null;
    }
}
