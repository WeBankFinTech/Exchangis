package com.webank.wedatasphere.exchangis.appconn.utils;

public class NumberUtils {
    public static Integer getInt(Object original){
        if(original instanceof Double){
            return ((Double) original).intValue();
        }
        return (Integer) original;
    }

    public static String parseDoubleString(String doubleString) {
        Double doubleValue = Double.parseDouble(doubleString);
        Integer intValue = doubleValue.intValue();
        return intValue.toString();
    }
}
