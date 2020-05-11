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
import com.webank.wedatasphere.exchangis.datasource.conns.HiveMeta;
import com.webank.wedatasphere.exchangis.datasource.conns.ldap.LdapConnector;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.ql.metadata.Hive;
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
 * 2018/9/03
 * Hive connection test
 */
@Service(PREFIX +"hive")
public class HiveConnCheck extends AbstractDataSourceConnCheck {

    private static final Logger logger = LoggerFactory.getLogger(HiveConnCheck.class);

    private static final String SEPARATOR = ",";
    @Resource
    private Configuration conf;

    @Override
    public void validate(DataSourceModel ma) throws Exception {
        // Validate if is Json
       Map<String, Object> params = ma.resolveParams();
       Set<String> keys = params.keySet();
       if(!keys.contains(PARAM_META_STORE_PATH)){
            throw new Exception(PARAM_META_STORE_PATH + " cannot be found");
       }
       String metaStorePaths = String.valueOf(params.get(PARAM_META_STORE_PATH));
       for(String metaStorePath : metaStorePaths.split(SEPARATOR)){
           if(!metaStorePath.trim().startsWith("thrift://")){
               throw new Exception("The structure of '" + metaStorePath +"' is illegal");
           }
       }
       if(!keys.contains(PARAM_HDFS_PATH)){
           throw new Exception(PARAM_HDFS_PATH + " cannot be found");
       }
       String hdfsPath = String.valueOf(params.get(PARAM_HDFS_PATH));
       if(!hdfsPath.startsWith(Hdfs.HDFS_PREFIX)){
           throw new Exception("The structure of '" + hdfsPath + "' is illegal");
       }
       validateKb(params);
    }


    @Override
    public void check(DataSource ds,  File file) throws Exception{
        Map<String, Object> parameters = ds.resolveParams();
        File kbFileTmp = null;
        try {
            if(isUseKb(parameters) && null == file){
                if(ds.getId() > 0){
                    kbFileTmp = getAuthFileFromDataSource(ds, AuthType.KERBERS, conf.getStoreTmp());
                    file = kbFileTmp;
                }
                if(null == file){
                    throw new RuntimeException("Keytab file has been lost");
                }
            }
            //Try to get connection of MetaStore
            testHiveMeta(parameters, file);
            //Try to get FileSystem
            testHiveHDFSRelated(parameters, file);
        }finally{
            if(null != kbFileTmp){
                if(!kbFileTmp.delete()){
                    logger.info("Delete kerberos temp file Failed, Path:" + kbFileTmp.getPath());
                }
            }
        }
    }

    private synchronized void testHiveMeta(Map<String, Object> params, File kbFile) throws Exception {
        boolean isUseKb = isUseKb(params);
        Hive client;
        if(isUseKb){
            client = HiveMeta.getClient(params, kbFile, conf.getKbPrincipleHive());
        }else{
            String userName = String.valueOf(params.getOrDefault(PARAM_LADP_USERNAME, ""));
            String password = String.valueOf(params.getOrDefault(PARAM_LADP_PASSWORD, ""));
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
            client = HiveMeta.getClient(params, userName);
        }
        client.getMSC().close();
    }

    private synchronized void testHiveHDFSRelated(Map<String, Object> params, File kbFile) throws Exception {
        boolean isUseKb = isUseKb(params);
        FileSystem fs;
        if(isUseKb){
            fs = Hdfs.getFileSystem(params, kbFile);
        }else {
            String userName = String.valueOf(params.getOrDefault(PARAM_LADP_USERNAME, ""));
            String password = String.valueOf(params.getOrDefault(PARAM_LADP_PASSWORD, ""));
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
            fs = Hdfs.getFileSystem(params, userName);
        }
        fs.close();
    }
}
