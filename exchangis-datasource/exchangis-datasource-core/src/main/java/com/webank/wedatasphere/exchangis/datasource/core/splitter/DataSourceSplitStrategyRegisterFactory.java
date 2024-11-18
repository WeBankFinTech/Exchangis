package com.webank.wedatasphere.exchangis.datasource.core.splitter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register factory
 */
public class DataSourceSplitStrategyRegisterFactory implements DataSourceSplitStrategyFactory {

    public void init(){
        // Register default split strategy
        this.register(new DataSourceFieldSplitStrategy());
    }
    /**
     * Strategy context
     */
    private final Map<String, DataSourceSplitStrategy> strategies = new ConcurrentHashMap<>();
    /**
     * Register split strategy
     * @param splitStrategy split strategy
     */
    public void register(DataSourceSplitStrategy splitStrategy){
        if (Objects.nonNull(splitStrategy)) {
            this.strategies.put(splitStrategy.name(), splitStrategy);
        }
    }
    @Override
    public DataSourceSplitStrategy getOrCreateSplitter(String name) {
        return strategies.get(name);
    }
}
