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

package com.webank.wedatasphere.exchangis.datasource.checks;

import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

@Service(PREFIX + "mysql")
public class MySQLConnCheck extends AbstractDataSourceConnCheck{
    private static final Logger LOG = LoggerFactory.getLogger(MySQLConnCheck.class);

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL_TEMPLATE = "jdbc:mysql://%s:%d";

    @Resource
    private Configuration conf;
    @Resource
    private DataSourceServiceImpl dataSourceService;
    @Override
    public void validate(DataSourceModel md) throws Exception {
        Map<String,Object> param = md.resolveParams();
        Set<String> keys = param.keySet();
        if(!keys.contains(PARAM_SFTP_HOST) ||
                StringUtils.isBlank(String.valueOf(param.get(PARAM_SFTP_HOST)))){
            throw new Exception(PARAM_SFTP_HOST + " cannot be null");
        }
        if(!keys.contains(PARAM_SFTP_PORT) ||
                StringUtils.isBlank(String.valueOf(param.get(PARAM_SFTP_PORT)))){
            throw new Exception(PARAM_SFTP_PORT + " cannot be null");
        }
        try{
            Integer.valueOf(String.valueOf(param.get(PARAM_SFTP_PORT)));
        }catch(NumberFormatException e){
            throw new Exception(PARAM_SFTP_PORT + " is not a number");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void check(DataSource ds, File file) throws Exception {
        Map<String,Object> param = ds.resolveParams();
        String host = String.valueOf(param.get(PARAM_SFTP_HOST));
        int port = Integer.parseInt(param.get(PARAM_SFTP_PORT).toString());
        String parameter = "";
        if(param.containsKey(PARAM_KEY_TDSQL_CONFIG)){
            Map<String,Object> param1 = (Map<String, Object>) param.get(PARAM_KEY_TDSQL_CONFIG);
            parameter = param1.entrySet().stream().map(
                    e->String.join("=", e.getKey(), String.valueOf(e.getValue()))
            ).collect(Collectors.joining("&"));
        }

        String url = String.format(URL_TEMPLATE, host, port);
        if(StringUtils.isNotEmpty(parameter)){
            url += "?" + parameter;
        }
        String username = String.valueOf(param.get(PARAM_DEFAULT_USERNAME));
        String password = String.valueOf(param.get(PARAM_DEFAULT_PASSWORD));
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(url,username,password);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            LOG.error("SQLException:" +e.getMessage(), e);
            throw new Exception(e);
        }finally {
            try {
                if(stmt != null){
                    stmt.close();
                }
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException e){
                LOG.error("SQLException:" +e.getMessage(), e);
            }
        }
    }
}
