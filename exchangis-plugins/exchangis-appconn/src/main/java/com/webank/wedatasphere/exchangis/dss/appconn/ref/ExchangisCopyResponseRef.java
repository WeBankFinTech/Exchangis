package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.DSSCommonResponseRef;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/4/24 12:52
 */
public class ExchangisCopyResponseRef extends DSSCommonResponseRef {
    private Map<String, Object> newJobContent;

    public ExchangisCopyResponseRef(Map<String, Object> jobContent, String responseBody) throws Exception {
        super(responseBody);
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }
}
