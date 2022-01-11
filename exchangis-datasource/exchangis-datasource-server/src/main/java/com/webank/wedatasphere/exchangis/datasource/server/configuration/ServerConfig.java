package com.webank.wedatasphere.exchangis.datasource.server.configuration;


import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.context.DefaultExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceLoader;
import com.webank.wedatasphere.exchangis.datasource.loader.loader.ExchangisDataSourceLoaderFactory;
import org.apache.linkis.common.exception.ErrorException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public ExchangisDataSourceContext context(MapperHook mapperHook) throws Exception {
        DefaultExchangisDataSourceContext context = new DefaultExchangisDataSourceContext();
        ExchangisDataSourceLoader loader = ExchangisDataSourceLoaderFactory.getLoader();
        loader.setContext(context);
        try {
            loader.init(mapperHook);
        } catch (Exception e) {
            throw new ErrorException(70059, e.getMessage());
        }
        return context;
    }

}
