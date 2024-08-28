package com.webank.wedatasphere.exchangis.datasource.core.utils;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
import com.webank.wedatasphere.exchangis.datasource.core.serialize.ParamKeySerializer;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DsKeyDefineUtil {

    @Resource
    private ParamKeySerializer paramKeySerializer;

    private static final List<String> DS_MODEL_FIELDS = Arrays.asList(
            "dcn_info", "elasticUrls", "host", "port", "tcp_port", "http_port", "address", "params"
    );

    public static final List<String> AUTH_KEYS = Arrays.asList(
            "username", "password", "appid", "objectid", "mkPrivate", "authType"
    );

    public static List<Map<String, Object>> mergeTypeKey(List<Map<String, Object>> list) {
        list.stream().forEach(typeKey -> {
            if (DS_MODEL_FIELDS.contains(typeKey.get("key"))) {
                typeKey.put("modelField", true);
            }
        });
        return list;
    }

    /**
     * 获取到dsModel的key值
     * 其中is_serialize表示需要被序列化为连接参数
     * @param parameter
     * @param dsModelTypeKeys
     * @return
     */
    public Map<String, Object> mergeDsModelParameter(String parameter, List<DataSourceModelTypeKey> dsModelTypeKeys) {
        Map<String, DataSourceModelTypeKey> modelTypeKeyMap = new HashMap<>();
        if (Objects.nonNull(dsModelTypeKeys)) {
            modelTypeKeyMap = dsModelTypeKeys.stream()
                    .collect(Collectors.toMap(DataSourceModelTypeKey::getKey, Function.identity()));
        }
        Map<String, Object> map = Json.fromJson(parameter, Map.class);
        Map<String, Object> modelParams = new HashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            DataSourceModelTypeKey typeKey = modelTypeKeyMap.get(key);
            if (Objects.nonNull(typeKey) && typeKey.getSerialize()) {
                modelParams.put(key, paramKeySerializer.serialize(value,
                        typeKey.getValueType(),
                        DataSourceParamKeyDefinition.ValueType.MAP));
            } else {
                modelParams.put(key, value);
            }
        }
        return modelParams;
    }

    public static List<Map<String, Object>> mergeDsModelTypeKey(List<DataSourceModelTypeKey> dsModelTypeKeys, List<Map<String, Object>> keyDefineMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        dsModelTypeKeys.forEach(item -> {
            result.add(toDsModelTypeKeyMap(item));
        });

        if (Objects.nonNull(keyDefineMap) || keyDefineMap.size() > 0) {
            for (Map<String, Object> keyDefine : keyDefineMap) {
                if (Objects.nonNull(keyDefine) && DS_MODEL_FIELDS.contains(keyDefine.get("key"))) {
                    result.add(keyDefine);
                }
            }
        }
        return result;
    }

    private static Map<String, Object> toDsModelTypeKeyMap(DataSourceModelTypeKey obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            map.put(f.getName(), value);
        }
        fields = clazz.getSuperclass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            map.put(f.getName(), value);
        }
        return map;
    }
}
