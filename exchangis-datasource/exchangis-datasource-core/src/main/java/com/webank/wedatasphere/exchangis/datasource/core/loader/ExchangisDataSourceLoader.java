package com.webank.wedatasphere.exchangis.datasource.core.loader;


import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import org.apache.linkis.common.conf.CommonVars;

import java.util.Objects;

public interface ExchangisDataSourceLoader {

    String EXCHANGIS_DIR_NAME = Objects.isNull(CommonVars.apply("wds.exchangis.datasource.extension.dir").getValue()) ? "exchangis-extds" : CommonVars.apply("wds.exchangis.datasource.extension.dir").getValue().toString();

    String PROPERTIES_NAME = "extds.properties";

    String LIB_NAME = "lib";

    String JAR_SUF_NAME = ".jar";

    String FILE_SCHEMA = "file://";

    void setClassLoader(ClassLoader classLoader);

    void setContext(ExchangisDataSourceContext context);

    void init(MapperHook mapperHook) throws Exception;

    ExchangisDataSource load(String dataSourceType);

    ExchangisDataSource get(String dataSourceType, boolean reload);

}
