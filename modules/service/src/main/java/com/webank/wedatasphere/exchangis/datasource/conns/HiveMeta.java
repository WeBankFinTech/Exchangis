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

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static org.apache.hadoop.fs.FileSystem.FS_DEFAULT_NAME_KEY;

/**
 * @author davidhua
 * 2018/9/14
 */
public class HiveMeta {
    private static final String DEFAULT_HIVE_USER = "hadoop";
    public static Hive getClient(Map<String, Object> params,
                                             File kerberos, String hivePrinciple) throws Exception {
        final HiveConf conf = new HiveConf();
        conf.setVar(HiveConf.ConfVars.METASTOREURIS, String.valueOf(params.get(PARAM_META_STORE_PATH)));
        conf.setVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL, "true");
        conf.setVar(HiveConf.ConfVars.METASTORE_KERBEROS_PRINCIPAL, hivePrinciple);
        conf.set("hadoop.security.authentication", "kerberos");
        conf.setBoolean(String.format("fs.%s.impl.disable.cache", URI.create(conf.get(FS_DEFAULT_NAME_KEY, "")).getScheme()), true);
        String principle = String.valueOf(params.get(PARAM_KERBEROS_FILE_PRINCILE));
        principle = principle.substring(0, principle.indexOf("@"));
        UserGroupInformation ugi = UserGroupInformationWrapper.loginUserFromKeytab(conf,
                principle, kerberos.getPath());
        return getHive(ugi, conf);
    }

    public static Hive getClient(Map<String, Object> params, String userName) throws Exception{
        final HiveConf conf = new HiveConf();
        if(StringUtils.isBlank(userName)){
            userName = System.getProperty("user.name", "");
        }
        conf.setVar(HiveConf.ConfVars.METASTOREURIS, String.valueOf(params.get(PARAM_META_STORE_PATH)));
        conf.setBoolean(String.format("fs.%s.impl.disable.cache", URI.create(conf.get(FS_DEFAULT_NAME_KEY, "")).getScheme()), true);
        UserGroupInformation ugi = UserGroupInformationWrapper.createProxyUser(conf, userName);
        return getHive(ugi, conf);
    }

    private static Hive getHive(UserGroupInformation ugi,  HiveConf conf) throws IOException, InterruptedException {
        return ugi.doAs((PrivilegedExceptionAction<Hive>) () -> {
            Hive hive = Hive.get(conf);
            hive.getMSC();
            //to remove thread Local vars
            Hive.set(null);
            return hive;
        });
    }
}
