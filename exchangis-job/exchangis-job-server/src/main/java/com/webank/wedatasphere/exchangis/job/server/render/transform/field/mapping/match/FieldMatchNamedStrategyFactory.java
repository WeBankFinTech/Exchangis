package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;


import java.util.concurrent.ConcurrentHashMap;

/**
 * Default strategy factory
 */
public class FieldMatchNamedStrategyFactory implements FieldMatchStrategyFactory{

    private final ConcurrentHashMap<String, FieldMatchStrategy> strategies = new ConcurrentHashMap<>();

    @Override
    public FieldMatchStrategy getOrCreateStrategy(String strategyName) {
        return strategies.get(strategyName);
    }

    /**
     * Register strategy entities
     * @param strategyName strategyName
     * @param strategy strategy
     */
    public void registerStrategy(String strategyName, FieldMatchStrategy strategy){
        this.strategies.putIfAbsent(strategyName, strategy);
    }
}
