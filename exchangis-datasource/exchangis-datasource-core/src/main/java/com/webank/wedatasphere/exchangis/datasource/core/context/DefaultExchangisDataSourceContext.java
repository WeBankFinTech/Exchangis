package com.webank.wedatasphere.exchangis.datasource.core.context;


import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceLoader;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultExchangisDataSourceContext implements ExchangisDataSourceContext {

    private final Map<String, ExchangisDataSource> dataSources = new ConcurrentHashMap<>(24);

    @Override
    public boolean registerDataSourceLoader(ExchangisDataSourceLoader loader) {
        return false;
    }

    @Override
    public void addExchangisDataSource(ExchangisDataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource required");
        String name = dataSource.name();
        dataSources.put(name, dataSource);
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
        if (Strings.isNullOrEmpty(type)) {
            return null;
        }
        return this.dataSources.get(type.trim().toUpperCase());
    }

    @Override
    public ExchangisDataSource getExchangisDataSource(Long dataSourceTypeId) {
        if (Objects.isNull(dataSourceTypeId)) {
            return null;
        }
        Collection<ExchangisDataSource> values = this.dataSources.values();
        for (ExchangisDataSource ds : values) {
            if (ds.id().equalsIgnoreCase(dataSourceTypeId+"")) {
                return ds;
            }
        }

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
