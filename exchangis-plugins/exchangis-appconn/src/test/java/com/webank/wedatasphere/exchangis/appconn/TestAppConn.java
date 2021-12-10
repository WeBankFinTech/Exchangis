package com.webank.wedatasphere.exchangis.appconn;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.HashMap;
import java.util.Map;

public class TestAppConn {
    private final static Gson gson = new Gson();
    public static void main(String[] args) throws JsonProcessingException {
        String str="{\"type\":\"HAWorkFlowContextID\",\"value\":\"{\\\"instance\\\":null,\\\"backupInstance\\\":null,\\\"user\\\":\\\"hdfs\\\",\\\"workspace\\\":\\\"bdapWorkspace\\\",\\\"project\\\":\\\"DWExchangis06\\\",\\\"flow\\\":\\\"DWWorkFlow01\\\",\\\"contextId\\\":\\\"8-8--cs_1_devcs_1_dev22\\\",\\\"version\\\":\\\"v000001\\\",\\\"env\\\":\\\"BDAP_DEV\\\"}\"}";

       Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(str, Map.class);
        //System.out.println(responseMap);
        Map<String,Object> map = new HashMap<>();
        map.put("contextID","{\"type\":\"HAWorkFlowContextID\",\"value\":{\"instance\":null}}");
        //contextID

        String contextID = map.get("contextID").toString();
        //System.out.println(responseMap);
       // Map responseMap2 = BDPJettyServerHelper.jacksonJson().readValue(responseMap1.get("contextID").toString(), Map.class);
       // JsonObject jsonObject = gson.fromJson(responseMap1.get("contextID").toString(), JsonObject.class);

    }
}
