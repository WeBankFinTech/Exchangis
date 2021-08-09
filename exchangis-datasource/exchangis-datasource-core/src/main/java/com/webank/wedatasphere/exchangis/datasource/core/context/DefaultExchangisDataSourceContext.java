package com.webank.wedatasphere.exchangis.datasource.core.context;


import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceLoader;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultExchangisDataSourceContext implements ExchangisDataSourceContext {

    private Map<String, ExchangisDataSource> dataSources = new ConcurrentHashMap<>(24);

    @Override
    public boolean registerDataSourceLoader(ExchangisDataSourceLoader loader) {
        return false;
    }

    @Override
    public void addExchangisDataSource(ExchangisDataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource required");
        String type = dataSource.type();
        dataSources.put(type, dataSource);
    }

    @Override
    public ExchangisDataSource removeExchangisDataSource(String type) {
        return null;
    }

    @Override
    public ExchangisDataSource updateExchangisDataSource(ExchangisDataSource dataSource) {
        return null;
    }

    @Override
    public ExchangisDataSource getExchangisDataSource(String type) {
        return null;
    }

    @Override
    public Collection<ExchangisDataSource> all() {
        return this.dataSources.values();
    }

    @Override
    public Set<String> keys() {
        return this.dataSources.keySet();
    }
}
