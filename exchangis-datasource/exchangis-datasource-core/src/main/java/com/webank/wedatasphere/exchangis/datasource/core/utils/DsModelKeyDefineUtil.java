package com.webank.wedatasphere.exchangis.datasource.core.utils;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;

import java.lang.reflect.Field;
import java.util.*;

public class DsModelKeyDefineUtil {

    private static final List<String> DS_FIELDS = Arrays.asList(
            "username", "password", "database", "databaseName", "instance"
    );

    public static List<DataSourceModelTypeKey> transferToDsModelTypeKey(List<Map<String, Object>> keyDefineMap) {
        List<DataSourceModelTypeKey> result = new ArrayList<>();
        if (Objects.isNull(keyDefineMap) || keyDefineMap.size() <= 0) {
            return result;
        }
        for (Map<String, Object> keyDefine : keyDefineMap) {
            DataSourceModelTypeKey dataSourceModelTypeKey = convertMapToTypeKey(keyDefine, DataSourceModelTypeKey.class);
            if (!isDsField(dataSourceModelTypeKey)) {
                result.add(dataSourceModelTypeKey);
            }
        }
        return null;
    }

    private static boolean isDsField(DataSourceModelTypeKey dataSourceModelTypeKey) {
        if (Objects.isNull(dataSourceModelTypeKey)) {
            return false;
        }
        return DS_FIELDS.contains(dataSourceModelTypeKey.getKey());
    }

    private static <T> T convertMapToTypeKey(Map<String, Object> map, Class<T> clazz) {
        T obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Field field = clazz.getDeclaredField(key);
                if (Objects.nonNull(field)) {
                    field.setAccessible(true);
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


}
