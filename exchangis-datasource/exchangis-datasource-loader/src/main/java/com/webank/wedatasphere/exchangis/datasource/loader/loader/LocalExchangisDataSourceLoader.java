package com.webank.wedatasphere.exchangis.datasource.loader.loader;

import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceDefLoader;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategy;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategyFactory;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategyRegisterFactory;
import com.webank.wedatasphere.exchangis.datasource.loader.clazzloader.ExchangisDataSourceClassLoader;
import com.webank.wedatasphere.exchangis.datasource.loader.utils.ExceptionHelper;
import com.webank.wedatasphere.exchangis.datasource.loader.utils.ExtDsUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class LocalExchangisDataSourceLoader implements ExchangisDataSourceDefLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExchangisDataSourceLoader.class);

    private ClassLoader classLoader;
    /**
     * Context
     */
    private ExchangisDataSourceContext context;

    /**
     * Split strategy factory
     */
    private DataSourceSplitStrategyFactory splitStrategyFactory;
    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setContext(ExchangisDataSourceContext context) {
        this.context = context;
    }

    @Override
    public void setSplitStrategyFactory(DataSourceSplitStrategyFactory splitStrategyFactory) {
        this.splitStrategyFactory = splitStrategyFactory;
    }

    @Override
    public void init(MapperHook mapperHook) throws Exception {
        // 初始化磁盘扫描加载
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        String loadClassPath =  Objects.requireNonNull(currentClassLoader.getResource(EXCHANGIS_DIR_NAME)).getPath();
        if (StringUtils.endsWith(loadClassPath, File.separator)) {
            loadClassPath = loadClassPath + File.separator;
        }
        String libPathUrl = loadClassPath + ".." + File.separator + EXCHANGIS_DIR_NAME;
        LOGGER.info("libPath url is {}",  libPathUrl);
        List<URL> jars = ExtDsUtils.getJarsUrlsOfPath(libPathUrl);
//        List<URL> jars = ExtDsUtils.getJarsUrlsOfPath(EXCHANGIS_DIR_NAME);
        ClassLoader classLoader = new ExchangisDataSourceClassLoader(jars.toArray(new URL[1]), currentClassLoader);

        List<String> classNames = ExtDsUtils.getExchangisExtDataSourceClassNames(libPathUrl, classLoader);
        for (String clazzName: classNames) {
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(clazzName);
            } catch (ClassNotFoundException e) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                ExceptionHelper.dealErrorException(70062, clazzName + " class not found ", e, ErrorException.class);
            }

            if (clazz == null) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            } else {
                ExchangisDataSourceDefinition dsType = (ExchangisDataSourceDefinition) clazz.newInstance();
                dsType.setMapperHook(mapperHook);
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                String splitStrategyName = dsType.splitStrategyName();
                if (StringUtils.isBlank(splitStrategyName)){
                    DataSourceSplitStrategy splitStrategy = dsType.splitStrategy();
                    if (Objects.nonNull(splitStrategy) && this.splitStrategyFactory instanceof DataSourceSplitStrategyRegisterFactory){
                        ((DataSourceSplitStrategyRegisterFactory) this.splitStrategyFactory).register(splitStrategy);
                        splitStrategyName = splitStrategy.name();
                    }
                }
                LOGGER.info("ExchangisDataSource => [class: {}, name: {}, type_id: {}, split_strategy: {}]",
                        dsType.getClass().toString(), dsType.name(), dsType.id(), StringUtils.isNoneBlank(splitStrategyName)? splitStrategyName : "NONE");
                context.addExchangisDsDefinition(dsType);
            }
        }

    }

    @Override
    public ExchangisDataSourceDefinition load(String dataSourceType) {
        return null;
    }

    @Override
    public ExchangisDataSourceDefinition get(String dataSourceType, boolean reload) {
        return null;
    }
}
