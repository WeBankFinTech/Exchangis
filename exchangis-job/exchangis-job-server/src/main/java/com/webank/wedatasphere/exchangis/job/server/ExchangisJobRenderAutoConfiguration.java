package com.webank.wedatasphere.exchangis.job.server;

import com.webank.wedatasphere.exchangis.job.server.mapper.JobTransformRuleDao;
import com.webank.wedatasphere.exchangis.job.server.render.transform.*;
import com.webank.wedatasphere.exchangis.job.server.render.transform.def.DefaultTransformDefineRulesFusion;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.DefaultFieldMappingRulesFusion;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.FieldMappingRulesFusion;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.FieldMappingTransformer;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.*;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.ProcessorTransformer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.Optional;

@Configuration
@DependsOn("springContextHolder")
public class ExchangisJobRenderAutoConfiguration {

    /**
     * Field match strategy factory
     * @return factory
     */
    @Bean
    @ConditionalOnMissingBean(FieldMatchNamedStrategyFactory.class)
    public FieldMatchStrategyFactory matchStrategyFactory(){
        FieldMatchNamedStrategyFactory namedStrategyFactory = new FieldMatchNamedStrategyFactory();
        namedStrategyFactory.registerStrategy(FieldAllMatchStrategy.ALL_MATCH, new FieldAllMatchStrategy());
        namedStrategyFactory.registerStrategy(FieldAllMatchIgnoreCaseStrategy.ALL_MATCH_IGNORE_CASE, new FieldAllMatchIgnoreCaseStrategy());
        namedStrategyFactory.registerStrategy(FieldCamelCaseMatchStrategy.CAMEL_CASE_MATCH, new FieldCamelCaseMatchStrategy());
        return namedStrategyFactory;
    }
    /**
     * Field mapping rule fusion
     * @param strategyFactory match strategy factory
     * @return rule fusion
     */
    @Bean
    @ConditionalOnMissingBean(FieldMappingRulesFusion.class)
    public FieldMappingRulesFusion fieldMappingRulesFusion(FieldMatchStrategyFactory strategyFactory){
        return new DefaultFieldMappingRulesFusion(strategyFactory);
    }
    /**
     * Transform definition rule fusion
     * @return fusion
     */
    @Bean
    public TransformRulesFusion<TransformDefine> defineRuleFusion(){
        return new DefaultTransformDefineRulesFusion();
    }
    /**
     * Field mapping transformer
     * @param rulesFusion rule fusion
     * @param transformRuleDao transform rule dao
     * @return transformer
     */
    @Bean
    public FieldMappingTransformer fieldMappingTransformer(FieldMappingRulesFusion rulesFusion, JobTransformRuleDao transformRuleDao){
        return new FieldMappingTransformer(rulesFusion, transformRuleDao);
    }
    /**
     * Processor transformer
     * @return transformer
     */
    @Bean
    public ProcessorTransformer processorTransformer(){
        return new ProcessorTransformer();
    }

    @Bean
    @ConditionalOnMissingBean(TransformerContainer.class)
    public TransformerContainer transformerContainer(List<Transformer> transformers){
        TransformerContainer container = new DefaultTransformContainer();
        Optional.ofNullable(transformers).ifPresent(elements -> {
            elements.forEach(element -> container.registerTransformer(element.name(), element));
        });
        return container;
    }
    
}
