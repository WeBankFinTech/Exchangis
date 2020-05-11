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
import com.webank.wedatasphere.exchangis.datasource.conns.Sftp;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.Set;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

/**
 * @author davidhua
 * 2019/5/20
 */
@Service(PREFIX + "sftp")
public class SftpConnCheck extends AbstractDataSourceConnCheck{
    private static final Logger logger = LoggerFactory.getLogger(SftpConnCheck.class);
    @Resource
    private Configuration conf;
    @Resource
    private DataSourceServiceImpl dataSourceService;
    @Override
    public void validate(DataSourceModel ma) throws Exception {
        Map<String, Object> params = ma.resolveParams();
        Set<String> keys = params.keySet();
        if(!keys.contains(PARAM_SFTP_HOST) ||
                StringUtils.isBlank(String.valueOf(params.get(PARAM_SFTP_HOST)))){
            throw new Exception(PARAM_SFTP_HOST + " cannot be null");
        }
        if(!keys.contains(PARAM_SFTP_PORT)){
            throw new Exception(PARAM_SFTP_PORT + " cannot be null");
        }
        try{
            Integer.valueOf(String.valueOf(params.get(PARAM_SFTP_PORT)));
        }catch(NumberFormatException e){
            throw new Exception(PARAM_SFTP_PORT + " is not a number");
        }
    }

    @Override
    public void check(DataSource ds, File file) throws Exception {
        Map<String, Object> parameters = ds.resolveParams();
        String host = String.valueOf(parameters.get(PARAM_SFTP_HOST));
        int port = Integer.parseInt(String.valueOf(parameters.get(PARAM_SFTP_PORT)));
        String username = String.valueOf(parameters.get(PARAM_DEFAULT_USERNAME));
        String password = String.valueOf(parameters.get(PARAM_DEFAULT_PASSWORD));
        String path = "";
        File keyFileTmp = null;
        try {
            String authType = String.valueOf(parameters.getOrDefault(PARAM_AUTH_TYPE, ""));
            if(StringUtils.isNotBlank(authType) && AuthType.KEYFILE.equals(authType)) {
                if (null == file && ds.getId() > 0) {
                    file = getAuthFileFromDataSource(ds, AuthType.KEYFILE, conf.getStoreTmp());
                    keyFileTmp = file;
                }
                if (null != file) {
                    path = file.getPath();
                }
            }
            Sftp.SftpConnection connection = Sftp.login(host, port, username,
                    path,
                    password, CONNECT_TIMEOUT_IN_SECONDS * 1000);
            connection.disconnect();
        }finally{
            if(null != keyFileTmp){
                if(!keyFileTmp.delete()){
                    logger.info("Delete key temp file Failed, Path:" + keyFileTmp.getPath());
                }
            }
        }
    }

}
