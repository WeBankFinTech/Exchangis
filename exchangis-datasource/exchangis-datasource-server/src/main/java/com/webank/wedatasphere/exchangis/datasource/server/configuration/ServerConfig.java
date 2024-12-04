package com.webank.wedatasphere.exchangis.datasource.server.configuration;


import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.context.DefaultExchangisDsContext;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;
import com.webank.wedatasphere.exchangis.datasource.loader.loader.ExchangisDataSourceLoaderFactory;
import org.apache.linkis.common.exception.ErrorException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public ExchangisDataSourceContext context(MapperHook mapperHook) throws Exception {
        DefaultExchangisDsContext context = new DefaultExchangisDsContext();
        ExchangisDataSourceDefLoader loader = ExchangisDataSourceLoaderFactory.getLoader();
        loader.setContext(context);
        try {
            loader.init(mapperHook);
        } catch (Exception e) {
            throw new ErrorException(70059, e.getMessage());
        }
        return context;
    }

}
