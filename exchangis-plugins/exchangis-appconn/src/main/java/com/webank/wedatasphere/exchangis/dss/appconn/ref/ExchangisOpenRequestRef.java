package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;

import java.util.Map;

/**
 * Open request ref
 */
public class ExchangisOpenRequestRef extends CommonRequestRefImpl implements OpenRequestRef {
    @Override
    @SuppressWarnings("unchecked")
    public String getName() {
        return ((Map<String, Object>)this.getParameters().get("params")).get("title").toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getType() {
        return ((Map<String, Object>)this.getParameters().get("node")).get("nodeType").toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getJobContent() {
        return ((Map<String, Object>)this.getParameters().get("params"));
    }
}
