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

package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.conns.MySQL;
import com.webank.wedatasphere.exchangis.datasource.conns.cache.ConnCacheManager;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;
import com.webank.wedatasphere.exchangis.datasource.service.AbstractMetaDbService;
import com.webank.wedatasphere.exchangis.datasource.service.MetaService;
import com.webank.wedatasphere.exchangis.datasource.service.MySQLMetaDbService;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author v_fwu
 * 2018/9/18
 */
@Service(MetaService.PREFIX + "mysql")
public class MySQLMetaDbServiceImpl extends AbstractMetaDbService<MySQL> implements MySQLMetaDbService {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLMetaDbServiceImpl.class);

    @Resource
    private DataSourceServiceImpl dataSourceService;

    @Resource
    private ConnCacheManager connCacheManager;

    @Resource
    private Configuration conf;

    @Override
    @PostConstruct
    public void init(){
        super.initCache(connCacheManager, conf);
    }

    @Override
    protected DataSource getDatasourceById(String dsId) {
        DataSource ds = dataSourceService.getDetail(dsId);
        if(!ds.getSourceType().toLowerCase().equals(TypeEnums.MYSQL.v())){
            LOG.error("Data source id: "+ dsId +", type:"+ds.getSourceType() +", is not hive");
            throw new EndPointException("exchange.mysql_meta.invalid.data_source.id", null,dsId);
        }
        ds.resolveParams();
        return ds;
    }

    @Override
    protected MySQL getDBConnection(DataSource ds, File keytab){
        try {
            return null;
        }catch(Exception e){
            LOG.error("Get MySQL MetaStore connection failed, message: " +e.getMessage(), e);
            throw new EndPointException("exchange.mysql_meta.error.connection.meta", e);
        }
    }

    @Override
    protected boolean isConnLock() {
        return true;
    }

    @Override
    protected MySQL getDBConnection(DataSource ds){
        try {
            Map<String,Object> param = ds.getParameterMap();
            return MySQL.createTdSQL(param);
        }catch(Exception e){
            LOG.error("Get MySQL MetaStore connection failed, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.mysql_meta.error.connection.meta", e);
        }
    }

    @Override
    public List<String> getDatabases0(MySQL mysql) {
        try {
            return mysql.getAllDBdatas();
        } catch (Exception e) {
            LOG.error("Get MySQL databases error, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.mysql_meta.failed.get.database_info", e);
        }
    }



    @Override
    public List<String> getTables0(MySQL mysql, String database) {
        try {
            return mysql.getAllTables(database);
        } catch (Exception e) {
            LOG.error("Get MySQL tables error, database: "+ database +", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.mysql_meta.get.table.failed", e,database);
        }
    }

    @Override
    public MetaPartitionInfo getPartitions0(MySQL mysql, String database, String table) {
        return null;
    }


    @Override
    public List<MetaColumnInfo> getColumns0(MySQL mysql, String database, String table) {
        try {
            return mysql.getColumn(database,table);
        }catch (Exception e){
            LOG.error("Get MySQL columns error, database: " + database+", tabke: " + table + ", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.mysql_meta.get.table.failed", e,database);
        }
    }


    @Override
    public String getPath(String dsId, String database, String table) {
        return null;
    }

    @Override
    public Properties getMetaStore(String dsId, String database, String table) {
        return null;
    }

    @Override
    public Properties getMetaStore(DataSource ds, String database, String table) {
        return null;
    }

    @Override
    public Properties getMetaStore(String dsId, String database, String table, Map<String, String> partSpec) {
        return null;
    }

    @Override
    public Properties getMetaStore(DataSource ds, String database, String table, Map<String, String> partSpec) {
        return null;
    }

    @Override
    public boolean isView(String dsId, String database, String table) {
        return false;
    }

    @Override
    public boolean isView(DataSource ds, String database, String table) {
        return false;
    }

    @Override
    public Table getRawTable(String dsId, String database, String table) {
        return null;
    }

    @Override
    public Table getRawTable(DataSource ds, String database, String table) {
        return null;
    }

    @Override
    public List<String> getPrimaryKeys(String dsId, String database, String table) {
        return super.getConnAndRun(dsId, conn -> this.getPrimaryKeys0(conn, database, table));
    }

    @Override
    public List<String> getPrimaryKeys(DataSource ds, String database, String table) {
        return super.getConnAndRun(ds, conn -> this.getPrimaryKeys0(conn, database, table));
    }

    private List<String> getPrimaryKeys0(MySQL mysql, String database, String table){
        return mysql.getPrimaryKeys(database, table);
    }
}
