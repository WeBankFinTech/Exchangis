package com.webank.wedatasphere.exchangis.datasource.server.configuration;


import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.context.DefaultExchangisDsContext;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;
import com.webank.wedatasphere.exchangis.datasource.core.serialize.DefaultDataSourceParamKeySerializer;
import com.webank.wedatasphere.exchangis.datasource.core.serialize.ParamKeySerializer;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategyFactory;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategyRegisterFactory;
import com.webank.wedatasphere.exchangis.datasource.loader.loader.ExchangisDataSourceLoaderFactory;
import org.apache.linkis.common.exception.ErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {
    /**
     * Split strategy factory
     * @return factory
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public DataSourceSplitStrategyFactory splitStrategyFactory(){
        return new DataSourceSplitStrategyRegisterFactory();
    }
    @Bean
    public ExchangisDataSourceContext context(DataSourceSplitStrategyFactory factory, MapperHook mapperHook) throws Exception {
        DefaultExchangisDsContext context = new DefaultExchangisDsContext();
        ExchangisDataSourceDefLoader loader = ExchangisDataSourceLoaderFactory.getLoader();
        loader.setContext(context);
        loader.setSplitStrategyFactory(factory);
        try {
            loader.init(mapperHook);
        } catch (Exception e) {
            throw new ErrorException(70059, e.getMessage());
        }
        return context;
    }

    @Bean
    @ConditionalOnMissingBean(ParamKeySerializer.class)
    public ParamKeySerializer paramKeySerializer(){
        return new DefaultDataSourceParamKeySerializer();
    }
}
