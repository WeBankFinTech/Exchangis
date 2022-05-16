package com.webank.wedatasphere.exchangis.datasource.server;

import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.LinkisMetadataInfoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configure the core beans
 */
@Configuration
public class ExchangisDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetadataInfoService.class)
    public MetadataInfoService metadataInfoService(){
        return new LinkisMetadataInfoService();
    }
}
