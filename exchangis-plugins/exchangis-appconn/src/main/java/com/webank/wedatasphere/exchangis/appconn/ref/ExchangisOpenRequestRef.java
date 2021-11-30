package com.webank.wedatasphere.exchangis.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;

import java.util.Map;

public class ExchangisOpenRequestRef extends CommonRequestRefImpl implements OpenRequestRef {

    @Override
    public String getName() {
        return ((Map<String, Object>)this.getParameters().get("params")).get("title").toString();
    }

    @Override
    public String getType() {
        return ((Map<String, Object>)this.getParameters().get("node")).get("nodeType").toString();
    }

    public Object getJobContent() {
        return ((Map<String, Object>)this.getParameters().get("params"));
    }

}
