package com.webank.wedatasphere.exchangis.engine.server;

import com.webank.wedatasphere.exchangis.engine.config.ExchangisEngineConfiguration;
import com.webank.wedatasphere.exchangis.engine.dao.EngineResourceDao;
import com.webank.wedatasphere.exchangis.engine.dao.EngineSettingsDao;
import com.webank.wedatasphere.exchangis.engine.manager.ExchangisEngineManager;
import com.webank.wedatasphere.exchangis.engine.resource.DefaultEngineResourcePathScanner;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourcePathScanner;
import com.webank.wedatasphere.exchangis.engine.server.manager.SpringExchangisEngineManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configure the beans in engine
 */
@Configuration
public class ExchangisEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EngineResourcePathScanner.class)
    public EngineResourcePathScanner resourcePathScanner(){
        return new DefaultEngineResourcePathScanner();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExchangisEngineManager.class)
    public ExchangisEngineManager engineManager(EngineResourceDao resourceDao,
                                                EngineSettingsDao settingsDao, EngineResourcePathScanner scanner){
        return new SpringExchangisEngineManager(ExchangisEngineConfiguration.ENGINE_RESOURCE_ROOT_PATH.getValue(),
                resourceDao, settingsDao, scanner);
    }
}
