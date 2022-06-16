package com.webank.wedatasphere.exchangis.project.server.utils;

import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;

import java.util.Arrays;

/**
 * Utils for restful
 */
public class ExchangisProjectRestfulUtils {


    @SafeVarargs
    public static Message dealOk(String msg, Pair<String, Object>... data){
        Message message = Message.ok(msg);
        Arrays.stream(data).forEach(p -> message.data(p.getKey(), p.getValue()));
        return message;
    }


}
