package com.webank.wedatasphere.exchangis.datasource.core.context;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;

import java.util.Collection;
import java.util.Set;

/**
 * Data source context
 */
public interface ExchangisDataSourceContext {

    boolean registerLoader(ExchangisDataSourceDefLoader loader);

    /**
     * Add ds definition
     * @param dataSource ds
     */
    void addExchangisDsDefinition(ExchangisDataSourceDefinition dataSource);

    /**
     * Remove definition
     * @param type type
     * @return definition
     */
    ExchangisDataSourceDefinition removeExchangisDsDefinition(String type);

    /**
     * Update definition
     * @param dataSource ds
     * @return definition
     */
    ExchangisDataSourceDefinition updateExchangisDsDefinition(ExchangisDataSourceDefinition dataSource);

    /**
     * Get ds definition
     * @param type type
     * @return definition
     */
    ExchangisDataSourceDefinition getExchangisDsDefinition(String type);

    /**
     * Get ds definition
     * @param dataSourceTypeId type id
     * @return definition
     */
    ExchangisDataSourceDefinition getExchangisDsDefinition(Long dataSourceTypeId);

    /**
     * All definition
     * @return definitions
     */
    Collection<ExchangisDataSourceDefinition> all();

    /**
     * Type names
     * @return set
     */
    Set<String> keys();

}
