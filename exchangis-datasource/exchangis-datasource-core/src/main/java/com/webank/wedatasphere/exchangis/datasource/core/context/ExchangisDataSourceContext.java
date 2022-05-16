package com.webank.wedatasphere.exchangis.datasource.core.context;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceLoader;

import java.util.Collection;
import java.util.Set;

public interface ExchangisDataSourceContext {

    boolean registerDataSourceLoader(ExchangisDataSourceLoader loader);

    void addExchangisDataSource(ExchangisDataSource dataSource);

    ExchangisDataSource removeExchangisDataSource(String type);

    ExchangisDataSource updateExchangisDataSource(ExchangisDataSource dataSource);

    ExchangisDataSource getExchangisDataSource(String type);

    ExchangisDataSource getExchangisDataSource(Long dataSourceTypeId);

    Collection<ExchangisDataSource> all();

    Set<String> keys();

}
