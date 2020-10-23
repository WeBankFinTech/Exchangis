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

package com.webank.wedatasphere.exchangis.datasource;

/**
 * @author davidhua
 * 2018/9/14
 */
public class Constants {

    public static final int CONN_DEFAULT_RETRY_COUNT = 2;

    public static final String AUTH_FILE_NAME = "auth";

    public static final String DEFAULT_ENDPOINT_SPLIT = ",";
    /**
     *  Common parameters
     */
    public static final String PARAM_KERBEROS_FILE_PRINCILE = "kerberosPrincipal";
    public static final String PARAM_KB_FILE_PATH = "kerberosKeytabFilePath";
    public static final String PARAM_LADP_USERNAME = "ldapUserName";
    public static final String PARAM_LADP_PASSWORD = "ldapUserPassword";
    public static final String PARAM_DEFAULT_PASSWORD = "password";
    public static final String PARAM_DEFAULT_USERNAME = "username";
    public static final String PARAM_KERBEROS_HOST_NAME = "hostname";
    public static final String PARAM_KERBEROS_REALM_INFO = "realminfo";
    public static final String PARAM_AUTH_TYPE = "authType";
    public static final String PARAM_KEY_FILE_PATH = "keyfilepath";
    public static final String PARAM_KEY_TDSQL_CONFIG = "connParams";
    public static final String PARAM_NULL_FORMAT = "nullFormat";
    /**
     *  HIVE data source parameters
     */
    public static final String PARAM_META_STORE_PATH = "hiveMetastoreUris";
    /**
     *  HDFS data source parameters
     */
    public static final String PARAM_KERBEROS_BOOLEAN = "haveKerberos";
    public static final String PARAM_HDFS_PATH = "defaultFS";
    public static final String PARAM_HADOOP_CONF_LIST= "hadoopConfig";
    /**
     *  SFTP data source parameters
     */
    public static final String PARAM_SFTP_HOST = "host";
    public static final String PARAM_SFTP_PORT = "port";
    /**
     *  Elastic search data source parameters
     */
    public static final String PARAM_ES_URLS = "elasticUrls";

    /**
     *  BinLog data source parameters
     */
    public static final String PARAM_BINLOG_DCN = "dcn";
    public static final String PARAM_BINLOG_SET = "set";
    public static final String PARAM_BINLOG_HOST = "host";
    public static final String PARAM_BINLOG_PORT = "port";

    /**
     *  Oracle data source parameters
     */
    public static final String PARAM_ORACLE_HOST = "host";
    public static final String PARAM_ORACLE_PORT = "port";
    public static final String PARAM_ORACLE_SERVICE_NAME  = "serviceName";
    public static final String PARAM_ORACLE_SID = "sid";
}
