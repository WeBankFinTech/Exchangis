package com.webank.wedatasphere.exchangis.dss.appconn.utils;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.apache.linkis.manager.label.entity.SerializableLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Appconn utils for exchangis
 */
public class AppConnUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AppConnUtils.class);
    /**
     * Invoke the "getStringValue" method in label entity and then concat each one
     * @param list label list
     * @return serialized string value
     */
    public static String serializeDssLabel(List<DSSLabel> list){
        String dssLabelStr = "";
        if(list != null && !list.isEmpty()){
            dssLabelStr = list.stream().map(SerializableLabel::getStringValue).collect(Collectors.joining(","));
        }
        return dssLabelStr;
    }

    @SuppressWarnings("unchecked")
    public static <T>T resolveParam(Map<String, Object> responseMap, String key, Class<T> type){
        try {
            ExchangisEntityRespResult.BasicMessageEntity<Object> entity = JsonExtension.convert(responseMap, ExchangisEntityRespResult.BasicMessageEntity.class, Object.class);
            Object data = entity.getData();
            if (Objects.nonNull(data) && data instanceof Map){
                Map<String, Object> dataMap = (Map<String, Object>)data;
                // TODO convert different type
                return (T)dataMap.get(key);
            }
        } catch (ExternalOperationFailedException e) {
            LOG.warn("Exception in resolving params: " + key, e);
        }
        return null;
    }

}
