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
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;

import java.util.List;

/**
 * Find the database's meta information
 * @author davidhua
 * 2018/9/18
 */
public interface MetaDbService extends MetaService{
    /**
     * Get all databases
     * @param dsId dataSource which is the type of db
     * @return
     */
    List<String> getDatabases(String dsId);
    List<String> getDatabases(DataSource dataSource);
    /**
     * Get all tables from database specified
     * @param dsId
     * @param database
     * @return
     */
    List<String> getTables(String dsId, String database);
    List<String> getTables(DataSource dataSource, String database);

    /**
     * Get all partitions from table specified
     * @param dsId
     * @param database
     * @param table
     * @return
     */
    MetaPartitionInfo getPartitions(String dsId, String database, String table);
    MetaPartitionInfo getPartitions(DataSource dataSource, String database, String table);

}
