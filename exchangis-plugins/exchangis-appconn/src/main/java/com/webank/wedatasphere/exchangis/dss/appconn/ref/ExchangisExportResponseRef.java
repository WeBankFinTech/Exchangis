package com.webank.wedatasphere.exchangis.dss.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisExportOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/9 17:39
 */
public class ExchangisExportResponseRef extends CommonResponseRef {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangisExportResponseRef.class);

    Map<String, Object> bmlResource;

    public ExchangisExportResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        LOG.info("responseBody123: {}", responseBody);
        LOG.info("responseMap123: {}", responseMap.toString());
        bmlResource = ((Map<String, Object>) responseMap.get("data"));
    }

    @Override
    public Map<String, Object> toMap() {return bmlResource;}
}
