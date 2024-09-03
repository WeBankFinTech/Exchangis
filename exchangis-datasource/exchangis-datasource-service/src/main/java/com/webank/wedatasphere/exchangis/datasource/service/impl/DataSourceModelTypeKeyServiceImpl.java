package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.utils.DsKeyDefineUtil;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelTypeKeyMapper;
import com.webank.wedatasphere.exchangis.datasource.service.AbstractLinkisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelTypeKeyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.request.GetKeyTypeDatasourceAction;
import org.apache.linkis.datasource.client.response.GetKeyTypeDatasourceResult;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_KEY_DEFINES_ERROR;

@Service
public class DataSourceModelTypeKeyServiceImpl extends AbstractLinkisDataSourceService implements DataSourceModelTypeKeyService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelTypeKeyServiceImpl.class);

    @Resource
    private DataSourceModelTypeKeyMapper dataSourceModelTypeKeyMapper;

    @Override
    public PageList<DataSourceModelTypeKey> findDsModelTypeKeyPageList(DataSourceModelTypeKeyQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        PageList<DataSourceModelTypeKey> page = new PageList<>(currentPage, pageSize, offset);
        List<DataSourceModelTypeKey> data = dataSourceModelTypeKeyMapper.findPage(pageQuery, new RowBounds(offset, pageSize));
        page.setData(data);
        return page;
    }

    @Override
    public List<Map<String, Object>> queryDsModelTypeKeys(String operator, DataSourceModelTypeKeyQuery pageQuery) throws ExchangisDataSourceException {
        String dsType = pageQuery.getDsType();
        if (StringUtils.isBlank(dsType)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(),
                    "dataSourceType should not be null");
        }
        List<DataSourceModelTypeKey> dsModelTypeKeys = dataSourceModelTypeKeyMapper.queryList(pageQuery);
        if (Objects.isNull(dsModelTypeKeys) || dsModelTypeKeys.size() <= 0 ) {
            return new ArrayList<>();
        }
        long typeId = dsModelTypeKeys.get(0).getDsTypeId();
        GetKeyTypeDatasourceResult result = rpcSend(ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient(), () ->
                        GetKeyTypeDatasourceAction.builder()
                                .setUser(operator).setDataSourceTypeId(typeId).build(),
                LinkisDataSourceRemoteClient::getKeyDefinitionsByType, CLIENT_DATASOURCE_GET_KEY_DEFINES_ERROR.getCode(),
                "");
        List<DataSourceParamKeyDefinition> definitions = result.getDataSourceParamKeyDefinitions();
        Map<String, Object> defMap = new HashMap<>();
        long order = -1L;
        for (DataSourceParamKeyDefinition def : definitions){
            String key = def.getKey();
            if (!DsKeyDefineUtil.AUTH_KEYS.contains(key)) {
                if (def.getId() > order){
                    order = def.getId();
                }
                defMap.put(def.getKey(), def);
            }
        }
        for (DataSourceModelTypeKey typeKey : dsModelTypeKeys){
            typeKey.setId(++order);
            defMap.put(typeKey.getKey(), typeKey);
        }
        // merge the key define
        return Json.convert(defMap.values(), List.class, Map.class);
    }

    @Override
    public long countDsModelTypeKey() {
        return dataSourceModelTypeKeyMapper.count();
    }
}
