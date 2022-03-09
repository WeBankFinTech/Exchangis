package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/9 17:39
 */
public class ExchangisExportResponseRef extends CommonResponseRef {

    Map<String, Object> bmlResource;

    public ExchangisExportResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        bmlResource = ((Map<String, Object>) responseMap.get("data"));
    }

    @Override
    public Map<String, Object> toMap() {return bmlResource;}
}
