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


import java.util.HashMap;
import java.util.Map;

/**
 * @author davidhua
 * 2018/10/29
 */
public enum TypeEnums {
    //dataSource types
    NONE(""),

    HIVE("hive"),

    HDFS("hdfs"),

    LOCAL_FS("local_fs"),

    SFTP("sftp"),

    ELASTICSEARCH("elasticsearch"),

    BINLOG("binlog"),

    MYSQL("mysql"),

    ORACLE("oracle");
    private static Map<String, TypeEnums> typeMap = new HashMap<>();
    private String name;
    TypeEnums(String name){
        this.name = name;
    }
    public String v(){
        return this.name;
    }
    public static TypeEnums type(String type){
        TypeEnums result = typeMap.get(type != null? type.toLowerCase() : type);
        return result == null?TypeEnums.NONE : result;
    }
    static{
        typeMap.put(HDFS.name, HDFS);
        typeMap.put(HIVE.name, HIVE);
        typeMap.put(LOCAL_FS.name, LOCAL_FS);
        typeMap.put(SFTP.name, SFTP);
        typeMap.put(ELASTICSEARCH.name, ELASTICSEARCH);
        typeMap.put(MYSQL.name, MYSQL);
        typeMap.put(ORACLE.name, ORACLE);
    }
}
