package com.webank.wedatasphere.exchangis.datasource.core.splitter;

/**
 * Split key for data source
 */
public class DataSourceSplitKey {
    /**
     * Split key
     */
    private final String splitKey;

    /**
     * Split values key
     */
    private final String[] splitValueKeys;

    public DataSourceSplitKey(String splitKey, String[] splitValueKeys){
        this.splitKey = splitKey;
        this.splitValueKeys = splitValueKeys;
    }

    public String getSplitKey() {
        return splitKey;
    }

    public String[] getSplitValueKeys() {
        return splitValueKeys;
    }
}
