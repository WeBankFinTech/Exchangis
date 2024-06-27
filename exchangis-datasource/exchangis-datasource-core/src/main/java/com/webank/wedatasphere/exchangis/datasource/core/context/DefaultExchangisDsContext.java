package com.webank.wedatasphere.exchangis.datasource.core.context;


import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultExchangisDsContext implements ExchangisDataSourceContext {

    private final Map<String, ExchangisDataSourceDefinition> dataSources = new ConcurrentHashMap<>(24);

    @Override
    public boolean registerLoader(ExchangisDataSourceDefLoader loader) {
        return false;
    }

    @Override
    public void addExchangisDsDefinition(ExchangisDataSourceDefinition dataSource) {
        Objects.requireNonNull(dataSource, "dataSource required");
        String name = dataSource.name();
        dataSources.put(name, dataSource);
    }

    @Override
    public ExchangisDataSourceDefinition removeExchangisDsDefinition(String type) {
        return null;
    }

    @Override
    public ExchangisDataSourceDefinition updateExchangisDsDefinition(ExchangisDataSourceDefinition dataSource) {
        return null;
    }

    @Override
    public ExchangisDataSourceDefinition getExchangisDsDefinition(String type) {
        if (Strings.isNullOrEmpty(type)) {
            return null;
        }
        return this.dataSources.get(type.trim().toUpperCase());
    }

    @Override
    public ExchangisDataSourceDefinition getExchangisDsDefinition(Long dataSourceTypeId) {
        if (Objects.isNull(dataSourceTypeId)) {
            return null;
        }
        Collection<ExchangisDataSourceDefinition> values = this.dataSources.values();
        for (ExchangisDataSourceDefinition ds : values) {
            if (ds.id().equalsIgnoreCase(dataSourceTypeId+"")) {
                return ds;
            }
        }

        return null;
    }

    @Override
    public Collection<ExchangisDataSourceDefinition> all() {
        return this.dataSources.values();
    }

    @Override
    public Set<String> keys() {
        return this.dataSources.keySet();
    }
}
