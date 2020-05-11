/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.datasource.conns;

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;

public class MySQL {
    private static final Logger logger = LoggerFactory.getLogger(MySQL.class);
    private static final String DBRIVER = "com.mysql.jdbc.Driver";
    private String host;
    private String port;
    private String username;
    private String password;
    private String parameter;

    private MySQL(String host, String port, String username, String password, String parameter) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.parameter = parameter;
    }

    public static MySQL createTdSQL(Map<String,Object> param) throws IOException, ClassNotFoundException {
        String host = String.valueOf(param.get(PARAM_SFTP_HOST));
        String port = param.get(PARAM_SFTP_PORT).toString();
        String username = String.valueOf(param.get(PARAM_DEFAULT_USERNAME));
        String password = String.valueOf(CryptoUtils.string2Object(String.valueOf(param.get(PARAM_DEFAULT_PASSWORD))));
        String params = "";
        if(param.containsKey(PARAM_KEY_TDSQL_CONFIG)){
            Map<String,Object> param1 = (Map<String, Object>) param.get(PARAM_KEY_TDSQL_CONFIG);
            params = param1.entrySet().stream().map(
                    e->String.join("=", e.getKey(), String.valueOf(e.getValue()))
            ).collect(Collectors.joining("&"));
        }
        return new MySQL(host,port,username,password,params);
    }

    private Connection getDbConnect(String database) throws Exception {
        Connection conn = null;
        try{
            Class.forName(DBRIVER);
            //设置连接参数 (***自己定义传递的参数***)
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            if(StringUtils.isNotEmpty(parameter)){
                url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + parameter;
            }
            conn = DriverManager.getConnection(url,username,password);
        }catch(Exception e){
            throw new EndPointException("exchange.mysql.obtain.database_info.failed", e);
        }
        // 建立了连接
        return conn;
    }

    public List<String> getAllDBdatas(){
        List<String> dataBaseName = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            Class.forName(DBRIVER);
            String url = "jdbc:mysql://" + host + ":" + port;
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("show databases");
            while (rs.next()){
                dataBaseName.add(rs.getString(1));
            }
        }catch(Exception e){
            logger.error("Failed to obtain database information",e.getMessage());
            throw new EndPointException("exchange.mysql.obtain.database_info.failed", e);
        }finally {
            closeResource(conn, stmt, rs);
        }
        return dataBaseName;
    }

    public List<String> getAllTables(String database){
        List<String> tableNames = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.getDbConnect(database);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                tableNames.add(rs.getString(1));
            }
        }catch (Exception e){
            throw new EndPointException("exchange.mysql.obtain.table_info.failed", e);
        }finally {
            closeResource(conn, stmt, rs);
        }
        return tableNames;
    }

    public List<MetaColumnInfo> getColumn(String database,String table) throws Exception{
        List<MetaColumnInfo> metaColumnInfos = new ArrayList<>();
        Connection conn = this.getDbConnect(database);
        String sql = "select * from `" + table + "` where 1 = 2";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSetMetaData meta = null;
        try {
            List<String> primaryKeys = getPrimaryKeys(conn, table);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                MetaColumnInfo info = new MetaColumnInfo();
                //index
                info.setIndex(i);
                //字段名称
                info.setName(meta.getColumnName(i));
                //类型
                info.setType(meta.getColumnTypeName(i));
                if(primaryKeys.contains(meta.getColumnName(i))){
                    info.setPrimaryKey(true);
                }
                metaColumnInfos.add(info);
            }
        } catch (SQLException e) {
            throw new EndPointException("exchange.mysql.obtain.field_info.failed", e);
        }finally {
            closeResource(conn, ps, rs);
        }
        return metaColumnInfos;
    }

    public List<String> getPrimaryKeys(String database, String table){
        Connection conn = null;
        try{
            conn = this.getDbConnect(database);
            return getPrimaryKeys(conn, table);
        }catch(Exception e){
            throw new EndPointException("exchange.mysql.obtain.field_info.failed", e);
        }finally{
            closeResource(conn, null, null);
        }
    }

    private List<String> getPrimaryKeys(Connection connection, String table) throws SQLException {
        ResultSet rs = null;
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            rs = dbMeta.getPrimaryKeys(null, null, table);
            while(rs.next()){
                primaryKeys.add(rs.getString("column_name"));
            }
            return primaryKeys;
        }finally{
            if(null != rs){
                closeResource(null, null, rs);
            }
        }
    }
    /**
     * close database resource
     * @param connection
     * @param statement
     * @param resultSet
     */
    private void closeResource(Connection connection,  Statement statement, ResultSet resultSet){
        try {
            if(null != resultSet && !resultSet.isClosed()) {
                resultSet.close();
            }
            if(null != statement && !statement.isClosed()){
                statement.close();
            }
            if(null != connection && !connection.isClosed()){
                connection.close();
            }
        }catch (SQLException e){
            logger.error("SQLException: " + e.getMessage(), e);
        }
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

}
