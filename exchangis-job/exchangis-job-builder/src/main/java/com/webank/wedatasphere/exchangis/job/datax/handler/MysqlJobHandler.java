package com.webank.wedatasphere.exchangis.job.datax.handler;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.datax.domain.Mapping;
import com.webank.wedatasphere.exchangis.job.datax.reader.MysqlReader;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.MysqlWriter;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.Connection;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;

import java.util.HashMap;
import java.util.Map;

public class MysqlJobHandler extends DataxJobHandler {
    @Override
    public Reader handleReader(ExchangisSubJob subjob, Long jobId) throws ExchangisDataSourceException {
        MysqlReader reader = new MysqlReader();
        reader.setName("mysqlreader");

        Map params = new HashMap<String, String>();
        //Map connectParams = dataSourceServiceservice.getGetDataSourceInfoResultDTO(jobId).getData().getInfo().getConnectParams();
        Map connectParams = new HashMap();
        connectParams.put("host", "127.0.0.1");
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
        return reader;
    }

    private String[] getSourceColumnArray(ExchangisSubJob subjob) {
        Mapping[] mappings = gson.fromJson(subjob.getTransforms().get("mapping").toString(), Mapping[].class);
        String[] columnArray = new String[mappings.length];
        for (int i = 0; i < mappings.length; i++) {
            columnArray[i] = mappings[i].getSource_field_name();
        }

        return columnArray;
    }

    private String[] getSinkColumnArray(ExchangisSubJob subjob) {
        Mapping[] mappings = gson.fromJson(subjob.getTransforms().get("mapping").toString(), Mapping[].class);
        String[] columnArray = new String[mappings.length];
        for (int i = 0; i < mappings.length; i++) {
            columnArray[i] = mappings[i].getSink_field_name();
        }

        return columnArray;
    }

    private String generateJdbcUrl(String host, String port, String databaseName) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        return url;
    }

    private String getTableNameFromId(String sourceId) {
        String[] source = sourceId.split("\\.");
        return source[3];
    }

    private String getDatabaseNameFromId(String sourceId) {
        String[] source = sourceId.split("\\.");
        return source[2];
    }

    @Override
    public Writer handleWriter(ExchangisSubJob subjob, Long jobId) {
        MysqlWriter writer = new MysqlWriter();
        writer.setName("mysqlwriter");

        Map params = new HashMap<String, String>();
        //Map connectParams = dataSourceService.getGetDataSourceInfoResultDTO(jobId).getData().getInfo().getConnectParams();
        Map connectParams = new HashMap();
        connectParams.put("host", "127.0.0.1");
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

        return writer;
    }
}
