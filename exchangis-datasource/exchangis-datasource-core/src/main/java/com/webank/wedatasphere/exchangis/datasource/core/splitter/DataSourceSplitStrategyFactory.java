package com.webank.wedatasphere.exchangis.datasource.core.splitter;

/**
 * Factory for data source splitters
 */
public interface DataSourceSplitStrategyFactory {
    /**
     *
     * @param name
     * @return
     */
    DataSourceSplitStrategy getOrCreateSplitter(String name);
}
