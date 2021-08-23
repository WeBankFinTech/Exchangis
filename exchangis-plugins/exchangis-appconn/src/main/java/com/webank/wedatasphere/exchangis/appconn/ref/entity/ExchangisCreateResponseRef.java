package com.webank.wedatasphere.exchangis.appconn.ref.entity;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.exchangis.appconn.utils.NumberUtils;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class ExchangisCreateResponseRef extends AbstractResponseRef {

    public ExchangisCreateResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        status = NumberUtils.getInt(responseMap.get("status"));
        if (status != 0) {
            errorMsg = responseMap.get("message").toString();
        }
//        widgetId = ((Map<String, Object>) responseMap.get("data")).get("widgetId").toString();
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
