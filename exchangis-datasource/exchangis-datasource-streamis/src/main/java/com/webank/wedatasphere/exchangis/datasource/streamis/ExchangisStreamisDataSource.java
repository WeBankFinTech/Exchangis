package com.webank.wedatasphere.exchangis.datasource.streamis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;

import java.util.List;

public abstract class ExchangisStreamisDataSource implements ExchangisDataSource {

    protected MapperHook mapperHook;

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

    @Override
    public LinkisDataSourceRemoteClient getDataSourceRemoteClient() {
        return ExchangisStreamisRemoteClient.getStreamisDataSourceRemoteClient();
    }

    @Override
    public LinkisMetaDataRemoteClient getMetaDataRemoteClient() {
        return ExchangisStreamisRemoteClient.getStreamisMetadataRemoteClient();
    }
}
