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
import com.webank.wedatasphere.exchangis.datasource.conns.Hdfs;
import com.webank.wedatasphere.exchangis.datasource.conns.ldap.LdapConnector;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;
/**
 * @author davidhua
 * 2019/4/2
 */
@Service(PREFIX + "hdfs")
public class HdfsConnCheck extends AbstractDataSourceConnCheck{

    private static final Logger logger = LoggerFactory.getLogger(HiveConnCheck.class);

    @Resource
    private Configuration conf;

    @PostConstruct
    public void init(){
        System.setProperty("java.security.krb5.conf", conf.getKbKrb5Path());
    }


    @Override
    public void validate(DataSourceModel ma) throws Exception {
        Map<String, Object> params = ma.resolveParams();
        Set<String> keys = params.keySet();
        if(!keys.contains(PARAM_HDFS_PATH)){
            throw new Exception(PARAM_HDFS_PATH + " cannot be found");
        }
        if(!String.valueOf(params.get(PARAM_HDFS_PATH))
                .startsWith(Hdfs.HDFS_PREFIX)){
            throw new Exception("The structure of '" + PARAM_HDFS_PATH + "' is illegal");
        }
        validateKb(params);
    }

    @Override
    public void check(DataSource ds, File file) throws Exception {
        Map<String, Object> parameters = ds.resolveParams();
        File kbFileTmp = null;
        try {
            boolean isUseKb = isUseKb(parameters);
            if(isUseKb && null == file){
                if( ds.getId() > 0){
                    new File(conf.getStoreTmp()).mkdirs();
                    kbFileTmp = getAuthFileFromDataSource(ds, AuthType.KERBERS, conf.getStoreTmp());
                    file = kbFileTmp;
                }
                if(null == file){
                    throw new RuntimeException("Keytab file has been lost");
                }
            }
            //Try to get FileSystem
            FileSystem fileSystem;
            if(isUseKb){
                fileSystem = Hdfs.getFileSystem(parameters, file);
            }else {
                String userName = String.valueOf(parameters.getOrDefault(PARAM_LADP_USERNAME, ""));
                String password = String.valueOf(parameters.getOrDefault(PARAM_LADP_PASSWORD, ""));
                if(StringUtils.isNotBlank(userName)) {
                    if (conf.isLdapSwitch()) {
                        LdapConnector connector = LdapConnector.getInstance(conf.getLdapUrl(), conf.getLdapBaseDn());
                        if (!connector.authenticate(userName, password)) {
                            throw new RuntimeException("LDAP Authenticate failed");
                        }
                    } else {
                        throw new RuntimeException("LDAP module does not be opened");
                    }
                }
                fileSystem = Hdfs.getFileSystem(parameters, userName);
            }
            fileSystem.close();
        }finally{
            if(null != kbFileTmp){
                if(!kbFileTmp.delete()){
                    logger.info("Delete kerberos temp file Failed, Path:" + kbFileTmp.getPath());
                }
            }
        }
    }
}
