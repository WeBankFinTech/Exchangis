package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.datax.domain.Mapping;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.Connection;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class DataxJobHandler implements JobHandler {


    @Autowired
    ExchangisDataSourceService dataSourceService;

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Override
    public void handleReader(ExchangisSubJob subjob, Long jobId, Reader reader) {

        if (reader.getName() == null) {
            reader.setName("reader");
        }

        Map params = new HashMap<String, String>();
        //Map connectParams = dataSourceServiceservice.getGetDataSourceInfoResultDTO(jobId).getData().getInfo().getConnectParams();
        Map connectParams = new HashMap();
        connectParams.put("host", "172.24.2.61");
        connectParams.put("port", "3306");
        connectParams.put("username", "root");
        connectParams.put("password", "123456");

        String host = connectParams.get("host").toString();
        String port = connectParams.get("port").toString();
        params.put("username", connectParams.get("username"));
        params.put("password", connectParams.get("password"));

        params.put("column", getSourceColumnArray(subjob));

        String sourceId = subjob.getDataSources().get("source_id").toString();
        String databaseName = getDatabaseNameFromId(sourceId);
        String tableName = getTableNameFromId(sourceId);
        String[] tableNameList = {tableName};
        Connection connection = new Connection();
        connection.setJdbcUrl(generateJdbcUrl(host, port, databaseName));
        connection.setTable(tableNameList);
        Connection[] connections = {connection};
        params.put("connection", connections);
        params.put("fileldDelimiter", ",");

        reader.setParameter(params);
    }


    @Override
    public void handleWriter(ExchangisSubJob subjob, Long jobId, Writer writer) {
        if (writer.getName() == null) {
            writer.setName("writer");
        }

        Map params = new HashMap<String, String>();
        //Map connectParams = dataSourceService.getGetDataSourceInfoResultDTO(jobId).getData().getInfo().getConnectParams();
        Map connectParams = new HashMap();
        connectParams.put("host", "172.24.2.61");
        connectParams.put("port", "3306");
        connectParams.put("username", "root");
        connectParams.put("password", "123456");

        String host = connectParams.get("host").toString();
        String port = connectParams.get("port").toString();
        params.put("username", connectParams.get("username"));
        params.put("password", connectParams.get("password"));

        params.put("column", getSinkColumnArray(subjob));

        String sinkId = subjob.getDataSources().get("sink_id").toString();
        String databaseName = getDatabaseNameFromId(sinkId);
        String tableName = getTableNameFromId(sinkId);
        String[] tableNameList = {tableName};
        Connection connection = new Connection();
        connection.setJdbcUrl(generateJdbcUrl(host, port, databaseName));
        connection.setTable(tableNameList);
        Connection[] connections = {connection};
        params.put("connection", connections);

        writer.setParameter(params);
    }


    protected String[] getSinkColumnArray(ExchangisSubJob subjob) {
        Mapping[] mappings = gson.fromJson(subjob.getTransforms().get("mapping").toString(), Mapping[].class);
        String[] columnArray = new String[mappings.length];
        for (int i = 0; i < mappings.length; i++) {
            columnArray[i] = mappings[i].getSink_field_name();
        }

        return columnArray;
    }

    protected String generateJdbcUrl(String host, String port, String databaseName) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        return url;
    }

    protected String getTableNameFromId(String sourceId) {
        String[] source = sourceId.split("\\.");
        return source[3];
    }

    protected String getDatabaseNameFromId(String sourceId) {
        String[] source = sourceId.split("\\.");
        return source[2];
    }


    protected String[] getSourceColumnArray(ExchangisSubJob subjob) {
        Mapping[] mappings = gson.fromJson(subjob.getTransforms().get("mapping").toString(), Mapping[].class);
        String[] columnArray = new String[mappings.length];
        for (int i = 0; i < mappings.length; i++) {
            columnArray[i] = mappings[i].getSource_field_name();
        }

        return columnArray;
    }
}
