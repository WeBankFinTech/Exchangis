package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.builder.ElementUIFactory;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceRenderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Default render service
 */
@Service
public class DefaultDataSourceRenderService implements DataSourceRenderService {

    /**
     * Default placeholder stored in system
     */
    private final static String[] DEFAULT_PLACEHOLDER =
            new String[]{"${timestamp}", "${yyyyMMdd}","${yyyy-MM-dd}", "${run_date}", "${run_date-1}", "${run_month_begin}", "${run_month_begin-1}"};

    /**
     * Metadata info service
     */
    @Resource
    private MetadataInfoService metadataInfoService;

    /**
     * Element Factory
     */
    @Resource
    private ElementUIFactory elementUIFactory;

    @Override
    public ElementUI<?> getPartitionAndRender(String userName,
                                              Long dataSourceId, String database, String table, ElementUI.Type uiType) throws ExchangisDataSourceException {
        List<String> partitionKeys = metadataInfoService.getPartitionKeys(userName, dataSourceId, database, table);
        Map<String, Object> renderParams = new LinkedHashMap<>();
        List<String> placeHolder = Arrays.asList(DEFAULT_PLACEHOLDER);
        partitionKeys.forEach(partition -> renderParams.putIfAbsent(partition, placeHolder));
        return elementUIFactory.createElement(uiType.name(), renderParams, Map.class);
    }
}
