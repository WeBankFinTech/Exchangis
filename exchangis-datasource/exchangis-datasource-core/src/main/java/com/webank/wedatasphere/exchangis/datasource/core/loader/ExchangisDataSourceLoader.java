package com.webank.wedatasphere.exchangis.datasource.core.loader;


import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;

public interface ExchangisDataSourceLoader {

    String APPCONN_DIR_NAME = "exchangis-extds";

    String PROPERTIES_NAME = "extds.properties";

    String LIB_NAME = "lib";

    String JAR_SUF_NAME = ".jar";

    String FILE_SCHEMA = "file://";

    void setClassLoader(ClassLoader classLoader);

    void setContext(ExchangisDataSourceContext context);

    void init() throws Exception;

    ExchangisDataSource load(String dataSourceType);

    ExchangisDataSource get(String dataSourceType, boolean reload);

}