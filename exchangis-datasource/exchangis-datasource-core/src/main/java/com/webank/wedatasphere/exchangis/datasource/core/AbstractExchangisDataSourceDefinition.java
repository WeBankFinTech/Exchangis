package com.webank.wedatasphere.exchangis.datasource.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.List;

public abstract class AbstractExchangisDataSourceDefinition implements ExchangisDataSourceDefinition {

    /**
     * Mapper hook from common module?
     */
    protected MapperHook mapperHook;
    /**
     * Type id
     */
    protected String id;
    @Override
    public String name() {
        return type().name;
    }


    @Override
    public String classifier() {
        return type().classifier;
    }


    @Override
    public void setMapperHook(MapperHook mapperHook) {
        this.mapperHook = mapperHook;
    }

    @Override
    public List<DataSourceType> getDataSourceTypes(String user) {
        return ExchangisDataSourceDefinition.super.getDataSourceTypes(user);
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

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return getDataSourceParamConfigs(type().name);
    }

    protected List<ExchangisJobParamConfig> getDataSourceParamConfigs(String type) {
        return getDataSourceParamConfigs(type, null);
    }


    protected List<ExchangisJobParamConfig> getDataSourceParamConfigs(String type, String dir) {
        ExchangisJobParamConfigMapper exchangisJobParamConfigMapper = this.mapperHook.getExchangisJobParamConfigMapper();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(dir)) {
            queryWrapper.eq("config_direction", dir);
        }
        queryWrapper.eq("type", type);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        return exchangisJobParamConfigMapper.selectList(queryWrapper);
    }

    /**
     * Data source type
     * @return type
     */
    protected abstract ExchangisDataSourceType type();
}
