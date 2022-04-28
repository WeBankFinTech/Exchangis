package com.webank.wedatasphere.exchangis;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Bean validator
 */
@Configuration
public class ValidatorConfiguration {
    @Bean
    public Validator validator(){
        return Validation.byProvider(HibernateValidator.class)
                .configure().failFast(true)
                .buildValidatorFactory().getValidator();
    }
}
