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
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.conns.HiveMeta;
import com.webank.wedatasphere.exchangis.datasource.conns.ldap.LdapConnector;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;
import com.webank.wedatasphere.exchangis.datasource.service.AbstractMetaDbService;
import com.webank.wedatasphere.exchangis.datasource.service.HiveMetaDbService;
import com.webank.wedatasphere.exchangis.datasource.service.MetaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.Partition;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;

/**
 * @author davidhua
 * 2018/9/18
 */
@Service(MetaService.PREFIX + "hive")
public class HiveMetaDbServiceImpl extends AbstractMetaDbService<Hive> implements HiveMetaDbService {
    private static final Logger LOG = LoggerFactory.getLogger(HiveMetaDbServiceImpl.class);

    @Resource
    private Configuration configuration;
    @Resource
    private DataSourceServiceImpl dataSourceService;

    @Override
    protected DataSource getDatasourceById(String dsId) {
        DataSource ds = dataSourceService.getDetail(dsId);
        if(!ds.getSourceType().toLowerCase().equals(TypeEnums.HIVE.v())){
            LOG.error("Data source id: "+ dsId +", type:"+ds.getSourceType() +", is not hive");
            throw new EndPointException("exchange.hive_meta.invalid.data_source.id", null, dsId);
        }
        ds.resolveParams();
        return ds;
    }

    @Override
    protected Hive getDBConnection(DataSource ds, File keytab){
        try {
            return HiveMeta.getClient(ds.getParameterMap(), keytab, configuration.getKbPrincipleHive());
        }catch(Exception e){
            LOG.error("Get Hive MetaStore connection failed, message: " +e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.connect.error", e);
        }
    }

    @Override
    protected boolean isConnLock() {
        return true;
    }

    @Override
    protected Hive getDBConnection(DataSource ds){
        try {
            String userName = String.valueOf(ds.getParameterMap().getOrDefault(PARAM_LADP_USERNAME, ""));
            if(StringUtils.isNotBlank(userName)) {
                String password =  String.valueOf(CryptoUtils.string2Object(
                        String.valueOf(ds.getParameterMap().getOrDefault(PARAM_LADP_PASSWORD, ""))));
                if (configuration.isLdapSwitch()) {
                    LdapConnector connector = LdapConnector.getInstance(configuration.getLdapUrl(), configuration.getLdapBaseDn());
                    if (!connector.authenticate(userName, password)) {
                        throw new RuntimeException("LDAP Authenticate failed");
                    }
                } else {
                    throw new RuntimeException("LDAP module does not be opened");
                }
            }
            return HiveMeta.getClient(ds.getParameterMap(), userName);
        }catch(Exception e){
            LOG.error("Get Hive MetaStore connection failed, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.connect.error", e);
        }
    }

    @Override
    public List<String> getDatabases0(Hive hive) {
        try {
            return hive.getAllDatabases();
        } catch (HiveException e) {
            LOG.error("Get Hive databases error, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.obtain.database.error", e);
        }
    }


    @Override
    public List<String> getTables0(Hive hive, String database) {
        try {
             return hive.getAllTables(database);
        } catch (HiveException e) {
            LOG.error("Get Hive tables error, database: "+ database +", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.get.table.failed",database, e);
        }
    }

    @Override
    public MetaPartitionInfo getPartitions0(Hive hive, String database, String table) {
        List<Partition> partitions;
        Table table1;
        try {
            table1 = hive.getTable(database, table);
            partitions = hive.getPartitions(hive.getTable(database, table));
        } catch (HiveException e) {
            LOG.error("Get Hive partitions error, database: " + database
                    + ", table: " +table+", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.get.information.failed", e,database,table);
        }
        MetaPartitionInfo info = new MetaPartitionInfo();
        List<FieldSchema> partitionKeys = table1.getPartitionKeys();
        List<String> partKeys = new ArrayList<>();
        partitionKeys.forEach(e -> partKeys.add(e.getName()));
        info.setPartKeys(partKeys);
        //Static partitions
        Map<String, MetaPartitionInfo.PartitionNode> pMap = new HashMap<>(20);
        MetaPartitionInfo.PartitionNode root = new MetaPartitionInfo.PartitionNode();
        info.setRoot(root);
        LOG.info("Get partitions: database {}, table {}" , database, table);
        long t = System.currentTimeMillis();
        for(Partition p : partitions){
            try {
                List<String> values = p.getValues();
                LOG.info("Get partitions: database {}, table {}, p {}, value {} " , database, table, p.getName(), Json.toJson(values, String.class));
                if(!partitionKeys.isEmpty()){
                    String parentNameValue = "";
                    for(int i = 0; i < values.size(); i++){
                        FieldSchema fieldSchema = partitionKeys.get(i);
                        String name = fieldSchema.getName();
                        String value = values.get(i);
                        String nameValue= name + "=" + value;
                        MetaPartitionInfo.PartitionNode node = new MetaPartitionInfo.PartitionNode();
                        if(i > 0){
                            MetaPartitionInfo.PartitionNode parent = pMap.get(parentNameValue);
                            parent.setName(name);
                            parent.getPartitions().putIfAbsent(value, node);
                        }else{
                            root.setName(name);
                            root.getPartitions().putIfAbsent(value, node);
                        }
                        parentNameValue += "/" + nameValue;
                        pMap.putIfAbsent(parentNameValue, node);
                    }
                }
            }catch(Exception e){
                LOG.info(e.getLocalizedMessage());
            }
        }
        LOG.info("Tree Build cost: {}", System.currentTimeMillis() - t);
        return info;
    }

    @Override
    public MetaPartitionInfo getPartitionKeys(String dsId, String database, String table) {
        return this.getConnAndRun(dsId , hive ->{
            Table rawTable = getRawTable(dsId, database, table);
            List<FieldSchema> partitionKeys = rawTable.getPartitionKeys();
            List<String> partKeys = new ArrayList<>();
            partitionKeys.forEach(e -> partKeys.add(e.getName()));
            MetaPartitionInfo info = new MetaPartitionInfo();
            info.setPartKeys(partKeys);
            MetaPartitionInfo.PartitionNode root = new MetaPartitionInfo.PartitionNode();
            info.setRoot(root);
            return info;
        });
    }

    @Override
    public List<MetaColumnInfo> getColumns0(Hive hive, String database, String table) {
        List<MetaColumnInfo> metaInfos = new ArrayList<>();
        Table tb;
        try {
            tb = hive.getTable(database, table);
        } catch (HiveException e) {
            LOG.error("Get Hive table error, database: " + database
                    + ", table: " +table+", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.obtain.information.failed", e,database,table);
        }
        if(tb.getTableType() == TableType.VIRTUAL_VIEW){
            throw new EndPointException("exchange.hive_meta.is.view.table", null,database,table);
        }
        for(StructField field : tb.getFields()){
            MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
            metaColumnInfo.setIndex(field.getFieldID());
            metaColumnInfo.setName(field.getFieldName());
            metaColumnInfo.setType(field.getFieldObjectInspector().getTypeName());
            metaInfos.add(metaColumnInfo);
        }
        return metaInfos;
    }

    @Override
    public String getPath(String dsId, String database, String table) {
        return super.getConnAndRun(dsId, hive ->{
            try{
                Table tb = hive.getTable(database, table);
                return tb.getPath().toUri().getPath();
            }catch(HiveException e){
                LOG.error("Get Hive table path error, database: " + database
                        + ", table: " +table+", message: " + e.getMessage(), e);
                throw new EndPointException("exchage.hive_meta.get.tablepath.failed", e,database,table);
            }
        });
    }

    @Override
    public Properties getMetaStore(String dsId, String database, String table) {
        return super.getConnAndRun(dsId, conn -> this.getMetaStore0(conn, database, table));
    }

    @Override
    public Properties getMetaStore(DataSource ds, String database, String table) {
        if(ds.getId() > 0){
            return this.getMetaStore(String.valueOf(ds.getId()), database, table);
        }
        return super.getConnAndRun(ds, conn -> this.getMetaStore0(conn, database, table));
    }

    @Override
    public Properties getMetaStore(String dsId, String database, String table, Map<String, String> partSpec) {
        return super.getConnAndRun(dsId, conn -> this.getMetaStore0(conn, database, table, partSpec));
    }

    @Override
    public Properties getMetaStore(DataSource ds, String database, String table, Map<String, String> partSpec) {
        if(ds.getId() > 0){
            return this.getMetaStore(String.valueOf(ds.getId()), database, table, partSpec);
        }
        return super.getConnAndRun(ds, conn -> this.getMetaStore0(conn, database, table, partSpec));
    }

    @Override
    public boolean isView(String dsId, String database, String table) {
        return super.getConnAndRun(dsId, conn -> this.isView0(conn, database, table));
    }

    @Override
    public boolean isView(DataSource ds, String database, String table) {
        if(ds.getId() > 0){
            return this.isView(String.valueOf(ds.getId()), database, table);
        }
        return super.getConnAndRun(ds, conn -> this.isView0(conn, database, table));
    }

    @Override
    public Table getRawTable(String dsId, String database, String table) {
        return super.getConnAndRun(dsId, conn -> this.getRawTable0(conn, database, table));
    }

    @Override
    public Table getRawTable(DataSource ds, String database, String table) {
        if(ds.getId() > 0){
            return this.getRawTable(String.valueOf(ds.getId()), database, table);
        }
        return super.getConnAndRun(ds, conn -> this.getRawTable0(conn, database, table));
    }


    private Properties getMetaStore0(Hive hive, String database, String table){
        Table table0 = getRawTable0(hive, database, table);
        return table0.getMetadata();
    }

    private Properties getMetaStore0(Hive hive, String database, String table, Map<String, String> partSpec){
        Table table0 = getRawTable0(hive, database ,table);
        Partition partition =  getPartition0(hive, table0, partSpec);
        if(null != partition){
            return partition.getMetadataFromPartitionSchema();
        }
        return null;
    }
    private boolean isView0(Hive hive, String database, String table){
            Table table0 = getRawTable0(hive, database, table);
            return table0.getTableType() == TableType.VIRTUAL_VIEW;
    }

    private Table getRawTable0(Hive hive, String database, String table){
        try{
            return hive.getTable(database, table);
        }catch(HiveException e){
            LOG.error("Get Hive table error, database: " + database
                    + ", table: " +table+", message: " + e.getMessage(), e);
            throw new EndPointException("exchange.hive_meta.obtain.information.failed", e,database,table);
        }
    }

    private Partition getPartition0(Hive hive, Table table, Map<String, String> partSpec){
        try{
            return hive.getPartition(table, partSpec, false);
        }catch(HiveException e){
            LOG.error("Get Hive partition error, database: " + table.getDbName()
                + ", table: " + table.getTableName() + ", message: " + e.getMessage() + ", partSpec: "
                    + Json.toJson(partSpec, null), e);
            throw new EndPointException("exchange.hive_meta.get.partition_info.failed", e,table.getDbName(),table.getTableName(),partSpec);
        }
    }
}
