/**
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.webank.wedatasphere.exchangis.job.server.utils;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        if (Objects.nonNull(applicationContext)) {
            return applicationContext.getBean(requiredType);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringContextHolder.applicationContext == null) {
            SpringContextHolder.applicationContext = applicationContext;
        }
    }

    @Override
    public void destroy() throws Exception {
        applicationContext = null;
    }

}
