package com.webank.wedatasphere.exchangis.datasource.loader.loader;

import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisDataSourceLoaderFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExchangisDataSourceLoaderFactory.class);

    private static Class<? extends ExchangisDataSourceDefLoader> clazz = LocalExchangisDataSourceLoader.class;
    private static ExchangisDataSourceDefLoader exchangisDataSourceDefLoader = null;

    public static ExchangisDataSourceDefLoader getLoader(){
        if (exchangisDataSourceDefLoader == null){
            synchronized (ExchangisDataSourceLoaderFactory.class){
                if (exchangisDataSourceDefLoader == null){
                    // 可以通过配置自行加载对应的类
                    CommonVars<String> apply = CommonVars.apply("exchangis.extds.loader.classname", "");
                    String className = apply.getValue();
                    if (StringUtils.isNotBlank(className)){
                        try{
                            clazz = ClassUtils.getClass(className);
                        }catch(ClassNotFoundException e){
                            logger.warn(String.format("Can not get ExchangisDataSourceLoader class %s, LocalExchangisDataSourceLoader will be used by default.", className), e);
                        }
                    }
                    try {
                        exchangisDataSourceDefLoader = clazz.newInstance();
                    } catch (Exception e) {
                        logger.error(String.format("Can not initialize ExchangisDataSourceLoader class %s.", clazz.getSimpleName()), e);
                    }
                    logger.info("Use {} to load all Exchangis Extension DataSources.", clazz.getSimpleName());
                }
            }
        }
        return exchangisDataSourceDefLoader;
    }

}
