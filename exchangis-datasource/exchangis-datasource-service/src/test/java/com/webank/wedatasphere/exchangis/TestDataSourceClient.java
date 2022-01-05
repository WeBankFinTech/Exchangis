package com.webank.wedatasphere.exchangis;

import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.DeleteDataSourceAction;
import com.webank.wedatasphere.linkis.datasource.client.request.QueryDataSourceAction;
import com.webank.wedatasphere.linkis.datasource.client.response.QueryDataSourceResult;
import com.webank.wedatasphere.linkis.httpclient.response.Result;

public class TestDataSourceClient {

    public static void main(String[] args) {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = TestExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
//        String responseBody;
//        try {
//            Result execute = linkisDataSourceRemoteClient.execute(
//                    new DeleteDataSourceAction.Builder().setUser("hdfs").setResourceId("12").builder()
//            );
//            responseBody = execute.getResponseBody();
//            System.out.println(responseBody);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("system")
                .setIdentifies("")
                .setCurrentPage(1)
                .setPageSize(500);
        QueryDataSourceAction action = builder.build();
        QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(action);
        System.out.println(result.getResponseBody());
        System.out.println(result.getAllDataSource());

    }

}
