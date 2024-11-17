package com.webank.wedatasphere.exchangis.datasource.core.serialize;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;

import java.util.*;

/**
 * Serializer
 */
public class DefaultDataSourceParamKeySerializer implements ParamKeySerializer{

    private static final String MAP_KEY_TAB = "k";

    private static final String MAP_VALUE_TAB = "v";

    @Override
    public String serialize(Object value, DataSourceParamKeyDefinition.ValueType valueType, DataSourceParamKeyDefinition.ValueType... nestValueType) {
        Class<?> type = valueType.getJavaType();
        if(isPrimitive(type)){
            return String.valueOf(value);
        } else {
            Class<?>[] subTypes = new Class<?>[0];
            if (Objects.nonNull(nestValueType) && nestValueType.length > 0){
                subTypes = new Class<?>[nestValueType.length];
                for (int i = 0 ; i < nestValueType.length; i ++){
                    subTypes[i] = nestValueType[i].getJavaType();
                }
            }
            if (type.equals(List.class)){
                if (subTypes.length > 0 && subTypes[0].equals(Map.class)){
                    List<Map<String, Object>> transform = Json.convert(value, type, subTypes);
                    if (Objects.nonNull(transform)) {
                        Map<String, Object> serial = new HashMap<>();
                        for (Map<String, Object> item : transform) {
                            Object mapKey = item.get(MAP_KEY_TAB);
                            if (Objects.nonNull(mapKey) && StringUtils.isNotBlank(mapKey.toString())){
                                serial.put(String.valueOf(mapKey), item.get(MAP_VALUE_TAB));
                            }
                        }
                        if (serial.size() > 0){
                            return Json.toJson(serial, null);
                        }
                        return Json.toJson(transform, null);
                    }
                }
            }
            return Json.toJson(value, null);
        }
    }

    public boolean isPrimitive(Class<?> type) {
        try {
            return type.isPrimitive() || ((Class) type.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        DataSourceModelTypeKey typeKey = new DataSourceModelTypeKey();
        typeKey.setDsType("tdsql");
        typeKey.setKey("params");
        typeKey.setName("连接参数");
        typeKey.setValueType(DataSourceParamKeyDefinition.ValueType.LIST);
        typeKey.setNestType(DataSourceParamKeyDefinition.ValueType.MAP);
        typeKey.setNestFields("{\"k\":\"参数名\",\"v\":\"参数值\"}");
        typeKey.setSerialize(true);
        ParamKeySerializer paramKeySerializer = new DefaultDataSourceParamKeySerializer();
        String parameter = "{\"params\":[{\"k\":\"timeout\",\"v\":\"36000\"}]}";
        Map<String, Object> map = Json.fromJson(parameter, Map.class);
        Object paramValue = map.get(typeKey.getKey());
        String parseValue = paramKeySerializer.serialize(paramValue,
                typeKey.getValueType(),
                DataSourceParamKeyDefinition.ValueType.MAP);
        System.out.println(parseValue);
    }

}
