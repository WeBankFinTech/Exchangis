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
import org.datanucleus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;

/**
 * Basic wrapper of oracle connection
 */
public class Oracle {
    private static final Logger LOG = LoggerFactory.getLogger(Oracle.class);
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_DB = "database";
    private String host;
    private String port;
    private String username;
    private String password;
    private String serviceName;
    private String sid;


    private Oracle(String host, String port, String username, String password,String serviceName,String sid) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serviceName = serviceName;
        this.sid = sid;
    }

    public static Oracle createOracle(Map<String,Object> param) throws IOException, ClassNotFoundException {
        String host = String.valueOf(param.get(PARAM_ORACLE_HOST));
        String port = param.get(PARAM_ORACLE_PORT).toString();
        String username = String.valueOf(param.get(PARAM_DEFAULT_USERNAME));
        String password = String.valueOf(CryptoUtils.string2Object(String.valueOf(param.get(PARAM_DEFAULT_PASSWORD))));
        String serviceName = String.valueOf(param.get(PARAM_ORACLE_SERVICE_NAME));
        String sid = String.valueOf(param.get(PARAM_ORACLE_SID));
        return new Oracle(host,port,username,password,serviceName,sid);
    }

    private Connection getDbConnect(String database) throws Exception {
        return getDbConnect();
    }

    private Connection getDbConnect() throws Exception {
        Connection conn = null;
        try{
            Class.forName(DRIVER);
            //Set custom connection parameters
            String url;
            if(StringUtils.notEmpty(serviceName))
                url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
            else if(StringUtils.notEmpty(sid))
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
            else throw new EndPointException("At least one of sid and serviceName cannot be empty!",new Exception());
            //Check if the username is sysdba, need to add 'sys as '
            if(username.matches("sysdba|sysoper"))
                username = "sys as " + username;
            conn = DriverManager.getConnection(url,username,password);
        }catch(Exception e){
            throw new EndPointException("exchange.oracle.obtain.database_info.failed", e);
        }
        return conn;
    }

    /**
     * Get all databases in server instance
     * @return name list
     */
    public List<String> getAllDatabases(){
        List<String> dataBaseName = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            Class.forName(DRIVER);
            conn = getDbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from v$database");
            while (rs.next()){
                dataBaseName.add(rs.getString("name"));
            }
        }catch(Exception e){
            if (e instanceof SQLException) {
                if (((SQLException) e).getErrorCode() == 942) {
                    dataBaseName.add(DEFAULT_DB);
                    return dataBaseName;
                }
            }
            LOG.error("Failed to obtain database information: [" + e.getMessage() + "]");
            throw new EndPointException("exchange.oracle.obtain.database_info.failed", e);
        }finally {
            closeResource(conn, stmt, rs);
        }
        return dataBaseName;
    }

    /**
     * Get all tables from sid
     * @param sid sid
     * @return name list
     */
    public List<String> getAllTables(String sid){
        List<String> tableNames = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.getDbConnect(sid);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select TABLE_NAME from user_tables");
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }catch (Exception e){
            throw new EndPointException("exchange.oracle.obtain.table_info.failed", e);
        }finally {
            closeResource(conn, stmt, rs);
        }
        return tableNames;
    }

    public List<MetaColumnInfo> getColumn(String database, String table) throws Exception{
        List<MetaColumnInfo> metaColumnInfos = new ArrayList<>();
        Connection conn = this.getDbConnect(database);
        String sql = "select * from " + table + " where 1 = 2";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSetMetaData meta;
        try {
            List<String> primaryKeys = getPrimaryKeys(conn, table);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                MetaColumnInfo info = new MetaColumnInfo();
                info.setIndex(i);
                info.setName(meta.getColumnName(i));
                info.setType(meta.getColumnTypeName(i));
                if(primaryKeys.contains(meta.getColumnName(i))){
                    info.setPrimaryKey(true);
                }
                metaColumnInfos.add(info);
            }
        } catch (SQLException e) {
            throw new EndPointException("exchange.oracle.obtain.field_info.failed", e);
        }finally {
            closeResource(conn, ps, rs);
        }
        return metaColumnInfos;
    }

    /**
     * Get primary keys
     * @param connection connection
     * @param table table name
     * @return key list
     * @throws SQLException
     */
    private List<String> getPrimaryKeys(Connection connection, String table) throws SQLException {
        ResultSet rs = null;
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            rs = dbMeta.getPrimaryKeys(null, null, table.toUpperCase());
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

    public List<String> getPrimaryKeys(String database, String table){
        Connection conn = null;
        try{
            conn = this.getDbConnect(database);
            return getPrimaryKeys(conn, table);
        }catch(Exception e){
            throw new EndPointException("exchange.oracle.obtain.field_info.failed", e);
        }finally{
            closeResource(conn, null, null);
        }
    }

    /**
     * Close database resource
     * @param connection connection
     * @param statement statement
     * @param resultSet result set
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
            LOG.error("SQLException: " + e.getMessage(), e);
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

