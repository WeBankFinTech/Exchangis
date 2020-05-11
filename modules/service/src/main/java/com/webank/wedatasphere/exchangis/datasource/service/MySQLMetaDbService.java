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

package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import org.apache.hadoop.hive.ql.metadata.Table;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author v_fwu
 * 2019/8/13
 */
public interface MySQLMetaDbService extends MetaDbService {
    /**
     * Get path
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    String getPath(String dsId, String database, String table);
    /**
     * Get MetaStore by 'database', 'table'
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    Properties getMetaStore(String dsId, String database, String table);
    Properties getMetaStore(DataSource ds, String database, String table);

    /**
     * Get MetaStore by 'database', 'table', 'partSpec'
     * @param dsId
     * @param database
     * @param table
     * @param partSpec
     * @return
     */
    Properties getMetaStore(String dsId, String database, String table, Map<String, String> partSpec);
    Properties getMetaStore(DataSource ds, String database, String table, Map<String, String> partSpec);
    /**
     * determine if the table's type is 'VIEW'
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    boolean isView(String dsId, String database, String table);
    boolean isView(DataSource ds, String database, String table);

    /**
     * Get raw type table
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    Table getRawTable(String dsId, String database, String table);
    Table getRawTable(DataSource ds, String database, String table);

    /**
     * Get primary keys
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    List<String> getPrimaryKeys(String dsId, String database, String table);
    List<String> getPrimaryKeys(DataSource ds, String database, String table);
}
