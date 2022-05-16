package com.webank.wedatasphere.exchangis.appconn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.HashMap;
import java.util.Map;

public class TestAppConn {
    private final static Gson gson = new Gson();
    public static void main(String[] args) throws JsonProcessingException {
        //String str="{\"method\":null,\"status\":0,\"message\":\"OK\",\"data\":{\"item\":{\"id\":\"1469200683600183298\",\"dssProjectId\":null,\"name\":\"DWExchangis06\",\"workspaceName\":\"DWExchangis06\",\"description\":\"测试不要删除\",\"tags\":\"\",\"editUsers\":\"\",\"viewUsers\":\"\",\"execUsers\":\"\",\"domain\":\"STANDALONE\"}}}";
        String str = "{\"route\":\"prod\"}";
        Map<String, Object> labels = new HashMap<>();
       Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(str, Map.class);
        System.out.println(responseMap);
        labels.put("labels", responseMap);
        System.out.println(labels);
        Map<String, Object> item = (Map<String, Object>) ((Map<String, Object>) responseMap.get("data")).get("item");
        System.out.println(item.get("id"));

    }
}
