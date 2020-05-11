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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static org.apache.hadoop.fs.FileSystem.FS_DEFAULT_NAME_KEY;

/**
 * @author davidhua
 * 2018/9/14
 */
public class Hdfs {
    public static final String HDFS_PREFIX = "hdfs://";

    public static FileSystem getFileSystem(Map<String, Object> params,
                                           File kerberos) throws Exception {
        Configuration conf = new Configuration();
        Object value = params.get(PARAM_HADOOP_CONF_LIST);
        if(value instanceof Map){
            Map listItem = (Map)value;
            listItem.forEach((k, v) -> conf.set(String.valueOf(k), String.valueOf(v)));
        }
        configuration(conf, params);
        String principle = String.valueOf(params.get(PARAM_KERBEROS_FILE_PRINCILE));
        conf.set("dfs.namenode.kerberos.principal", principle);
        principle = principle.substring(0, principle.indexOf("@"));
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("com.sum.security.auth.module.Krb5LoginModule", "required");
        conf.setBoolean("hadoop.security.authorization", true);
        UserGroupInformation ugi = UserGroupInformationWrapper.loginUserFromKeytab(conf,
                principle, kerberos.getPath());
        return ugi.doAs((PrivilegedExceptionAction<FileSystem>)() -> {
            FileSystem f = FileSystem.get(conf);
            f.exists(new Path("/"));
            return f;
        });
    }

    public static FileSystem getFileSystem(Map<String, Object> params, String userName) throws Exception{
        Configuration conf = new Configuration();
        Object value = params.get(PARAM_HADOOP_CONF_LIST);
        if(value instanceof Map){
            Map listItem = (Map)value;
            listItem.forEach((k, v) -> conf.set(String.valueOf(k), String.valueOf(v)));
        }
        configuration(conf, params);
        if(StringUtils.isBlank(userName)){
            userName = System.getProperty("user.name", "");
        }
        UserGroupInformation ugi = UserGroupInformationWrapper.createRemoteUser(conf, userName);
        return ugi.doAs((PrivilegedExceptionAction<FileSystem>) () ->{
            FileSystem f = FileSystem.get(conf);
            f.exists(new Path("/"));
            return f;
        });
    }

    private static void configuration(Configuration conf, Map<String, Object> params){
        conf.set("fs.defaultFS", String.valueOf(params.get(PARAM_HDFS_PATH)));
        conf.setInt("ipc.client.connect.max.retries", CONN_DEFAULT_RETRY_COUNT);
        conf.setInt("dfs.client.failover.max.attempts", CONN_DEFAULT_RETRY_COUNT);
        conf.setBoolean(String.format("fs.%s.impl.disable.cache", URI.create(conf.get(FS_DEFAULT_NAME_KEY, "")).getScheme()), true);
    }
}
