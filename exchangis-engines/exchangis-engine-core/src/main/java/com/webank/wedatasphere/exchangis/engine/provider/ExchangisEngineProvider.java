package com.webank.wedatasphere.exchangis.engine.provider;

import com.webank.wedatasphere.exchangis.engine.ExchangisEngine;

import java.util.Map;

public interface ExchangisEngineProvider {


   ExchangisEngine getEngines(Map<String, Object> params);
}
