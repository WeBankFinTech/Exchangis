package com.webank.wedatasphere.exchangis.dao.hook;

import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperHook {

    private ExchangisJobInfoMapper exchangisJobInfoMapper;
    private ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;

    @Autowired
    public MapperHook(ExchangisJobInfoMapper exchangisJobInfoMapper, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.exchangisJobInfoMapper = exchangisJobInfoMapper;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    public ExchangisJobInfoMapper getExchangisJobInfoMapper() {
        return exchangisJobInfoMapper;
    }

    public void setExchangisJobInfoMapper(ExchangisJobInfoMapper exchangisJobInfoMapper) {
        this.exchangisJobInfoMapper = exchangisJobInfoMapper;
    }

    public ExchangisJobParamConfigMapper getExchangisJobParamConfigMapper() {
        return exchangisJobParamConfigMapper;
    }

    public void setExchangisJobParamConfigMapper(ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }
}
