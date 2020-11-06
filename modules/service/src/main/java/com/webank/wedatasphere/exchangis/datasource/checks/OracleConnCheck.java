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

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.Constants.PARAM_DEFAULT_PASSWORD;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

/**
 * Validation of oracle connection
 */
@Service(PREFIX + "oracle")
public class OracleConnCheck extends AbstractDataSourceConnCheck {
    private static final Logger logger = LoggerFactory.getLogger(OracleConnCheck.class);

    //oracle driver
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    @Resource
    private Configuration conf;
    @Resource
    private DataSourceServiceImpl dataSourceService;
    @Override
    public void validate(DataSourceModel md) throws Exception {
        Map<String,Object> param = md.resolveParams();
        Set<String> keys = param.keySet();
        if(!keys.contains(PARAM_ORACLE_HOST) ||
                StringUtils.isBlank(String.valueOf(param.get(PARAM_ORACLE_HOST)))){
            throw new Exception(PARAM_SFTP_HOST + " cannot be null");
        }
        if(!keys.contains(PARAM_ORACLE_PORT) ||
                StringUtils.isBlank(String.valueOf(param.get(PARAM_ORACLE_PORT)))){
            throw new Exception(PARAM_ORACLE_PORT + " cannot be null");
        }
        if(!keys.contains(PARAM_ORACLE_SERVICE_NAME) ||
                StringUtils.isBlank(String.valueOf(param.get(PARAM_ORACLE_SERVICE_NAME)))) {
            if(!keys.contains(PARAM_ORACLE_SID) ||
                    StringUtils.isBlank(String.valueOf(param.get(PARAM_ORACLE_SID)))) {
                throw new Exception(PARAM_ORACLE_SERVICE_NAME + " and " + PARAM_ORACLE_SID + "at least one cannot be null");
            }
        }
        if(keys.contains(PARAM_DEFAULT_USERNAME) && String.valueOf(param.get(PARAM_DEFAULT_USERNAME)).equals("sys")) {
            throw new Exception("connection as SYS should be as SYSDBA or SYSOPER!");
        }
        try{
            Integer.valueOf(String.valueOf(param.get(PARAM_ORACLE_PORT)));
        }catch(NumberFormatException e){
            throw new Exception(PARAM_ORACLE_PORT + " is not a number");
        }
    }

    @Override
    public void check(DataSource ds, File file) throws Exception {
        Map<String,Object> param = ds.resolveParams();
        String host = String.valueOf(param.get(PARAM_ORACLE_HOST));
        int port = Integer.parseInt(param.get(PARAM_ORACLE_PORT).toString());
        String serviceName = String.valueOf(param.get(PARAM_ORACLE_SERVICE_NAME));
        String sid         = String.valueOf(param.get(PARAM_ORACLE_SID));
        String url;
        if(StringUtils.isEmpty(serviceName)) {
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
        }
        else {
            url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
        }

        String username = String.valueOf(param.get(PARAM_DEFAULT_USERNAME));
        String password = String.valueOf(param.get(PARAM_DEFAULT_PASSWORD));
        if(username.matches("sysdba|sysoper"))
            username = "sys as " + username;
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(DRIVER);
            conn= DriverManager.getConnection(url,username,password);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            logger.error("SQLException:" +e.getMessage(), e);
            throw new Exception(e.getMessage());
        }finally {
            try {
                if(stmt != null){
                    stmt.close();
                }
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException e){
                logger.error("SQLException:" +e.getMessage(), e);
            }
        }
    }
}

