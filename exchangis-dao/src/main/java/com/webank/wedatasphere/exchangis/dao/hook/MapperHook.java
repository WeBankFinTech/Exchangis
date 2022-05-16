package com.webank.wedatasphere.exchangis.dao.hook;

import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperHook {

    private ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;

    @Autowired
    public MapperHook(ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    public ExchangisJobParamConfigMapper getExchangisJobParamConfigMapper() {
        return exchangisJobParamConfigMapper;
    }

    public void setExchangisJobParamConfigMapper(ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }
}
