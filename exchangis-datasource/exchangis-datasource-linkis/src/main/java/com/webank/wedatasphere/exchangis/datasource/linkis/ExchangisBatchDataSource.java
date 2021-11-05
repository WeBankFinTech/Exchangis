package com.webank.wedatasphere.exchangis.datasource.linkis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.GetAllDataSourceTypesAction;
import com.webank.wedatasphere.linkis.datasource.client.response.GetAllDataSourceTypesResult;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ExchangisBatchDataSource implements ExchangisDataSource {

    protected MapperHook mapperHook;
    protected String id;

    @Override
    public void setMapperHook(MapperHook mapperHook) {
        this.mapperHook = mapperHook;
    }

    protected List<ExchangisJobParamConfig> getDataSourceParamConfigs(String type) {
        ExchangisJobParamConfigMapper exchangisJobParamConfigMapper = this.mapperHook.getExchangisJobParamConfigMapper();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        return exchangisJobParamConfigMapper.selectList(queryWrapper);
    }

    protected List<ExchangisJobParamConfig> getDataSourceParamConfigs(String type, String dir) {
        ExchangisJobParamConfigMapper exchangisJobParamConfigMapper = this.mapperHook.getExchangisJobParamConfigMapper();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_direction", dir);
        queryWrapper.eq("type", type);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        return exchangisJobParamConfigMapper.selectList(queryWrapper);
    }

    @Override
    public LinkisDataSourceRemoteClient getDataSourceRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
    }

    @Override
    public LinkisMetaDataRemoteClient getMetaDataRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisMetadataRemoteClient();
    }

    @Override
    public String id() {
        if (null == id || id.equalsIgnoreCase("")) {
            List<DataSourceType> types = getDataSourceTypes("hdfs");
            for (DataSourceType type : types) {
                if (type.getName().equalsIgnoreCase(name())) {
                    this.id = type.getId();
                }
            }
        }
        return this.id;
    }
}
