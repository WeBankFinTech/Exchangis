package com.webank.wedatasphere.exchangis.datasource.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public abstract class AbstractExchangisDataSourceDefinition implements ExchangisDataSourceDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExchangisDataSourceDefinition.class);

    /**
     * Mapper hook from common module?
     */
    protected MapperHook mapperHook;

    /**
     * Split strategy
     */
    private DataSourceSplitStrategy splitStrategy;
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
        try {
            if (null == id || id.equalsIgnoreCase("")) {
                List<DataSourceType> types = getDataSourceTypes("hdfs");
                for (DataSourceType type : types) {
                    if (type.getName().equalsIgnoreCase(name())) {
                        this.id = type.getId();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to fetch datasource id, please check the linkis server");
            throw e;
        }
        return this.id;
    }

    @Override
    public final DataSourceSplitStrategy splitStrategy() {
        if (Objects.isNull(splitStrategy)){
            synchronized (this){
                this.splitStrategy = defineSplitStrategy();
            }
        }
        return splitStrategy;
    }

    @Override
    public DataSourceSplitStrategy getSplitStrategy() {
        return this.splitStrategy;
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

    /**
     * Define split strategy
     * @return split strategy
     */
    protected DataSourceSplitStrategy defineSplitStrategy(){
        return null;
    }
}
