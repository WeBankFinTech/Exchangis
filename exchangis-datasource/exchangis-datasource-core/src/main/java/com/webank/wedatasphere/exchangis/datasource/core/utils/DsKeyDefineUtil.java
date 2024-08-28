package com.webank.wedatasphere.exchangis.datasource.core.utils;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;

import java.lang.reflect.Field;
import java.util.*;

public class DsKeyDefineUtil {

    private static final List<String> DS_MODEL_FIELDS = Arrays.asList(
            "dcn_info", "elasticUrls", "host", "port", "tcp_port", "http_port", "address", "params"
    );

    public static List<Map<String, Object>> mergeTypeKey(List<Map<String, Object>> list) {
        list.stream().forEach(typeKey -> {
            if (DS_MODEL_FIELDS.contains(typeKey.get("key"))) {
                typeKey.put("modelField", true);
            }
        });
        return list;
    }

    public static List<Map<String, Object>> mergeDsModelTypeKey(List<DataSourceModelTypeKey> dsModelTypeKeys, List<Map<String, Object>> keyDefineMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        dsModelTypeKeys.forEach(item -> {
            result.add(toDsModelTypeKeyMap(item));
        });

        if (Objects.nonNull(keyDefineMap) || keyDefineMap.size() > 0) {
            for (Map<String, Object> keyDefine : keyDefineMap) {
                if (Objects.isNull(keyDefine) && DS_MODEL_FIELDS.contains(keyDefine.get("key"))) {
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

    public static void main(String[] args) {
        DataSourceModelTypeKey typeKey = new DataSourceModelTypeKey();
        typeKey.setKey("elasticUrls");
        typeKey.setName("elasticUrls");
        typeKey.setDsTypeId(30L);
        typeKey.setDescription("desc1");

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", 157L);
        map.put("key", "params");
        map.put("name", "连接参数");
        map.put("valueType", "TEXT");
        map.put("scope", "ENV");
        map.put("require", false);
        List<Map<String, Object>> maps = mergeDsModelTypeKey(Arrays.asList(typeKey), Arrays.asList(map));
        System.out.println(Objects.isNull(maps));
    }

//    private static <T> T convertMapToTypeKey(Map<String, Object> map, Class<T> clazz) {
//        T obj = null;
//        try {
//            obj = clazz.getDeclaredConstructor().newInstance();
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                Field[] fields = clazz.getDeclaredFields();
//                Field field = null;
//                for (Field f : fields) {
//                    if (f.getName().equals(key)) {
//                        field = f;
//                        break;
//                    }
//                }
//                if (field == null && clazz.getSuperclass() != null) {
//                    fields = clazz.getSuperclass().getDeclaredFields();
//                    for (Field f : fields) {
//                        if (f.getName().equals(key)) {
//                            field = f;
//                            break;
//                        }
//                    }
//                }
//                if (Objects.nonNull(field)) {
//                    field.setAccessible(true);
//                    field.set(obj, value);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return obj;
//    }

}
