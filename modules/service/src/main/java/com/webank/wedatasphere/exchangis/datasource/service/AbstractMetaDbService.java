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

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.conns.cache.ConnCacheManager;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Abstract service for getting data source's meta information from database
 * include cache component and connection lock
 * @author davidhua
 * 2019/1/11
 */
public abstract class AbstractMetaDbService<C> implements MetaDbService {
    private static final String TEMP_PATH = "/tmp/";
    private static final Logger logger = LoggerFactory.getLogger(AbstractMetaDbService.class);
    private static final String CONN_CACHE_STORED = "_STORED";
    private static final String CONN_CACHE_REQ = "_REQ";
    private static final String TRUE = "true";
    @Resource
    private Configuration conf;

    @Resource
    private ConnCacheManager connCacheManager;

    /**
     * Caching connections which built by data source object stored
     */
    protected LoadingCache<String, ConnectionContext> storedCache;
    /**
     * Caching connections which built by data source object requested with
     */
    protected Cache<String, ConnectionContext> reqCache;
    /**
     * Need connection lock
     * @return
     */
    protected boolean isConnLock(){
        return false;
    }

    @PostConstruct
    public void init(){
        initCache(connCacheManager, conf);
    }
    /**
     * If want to use cache component, you should invoke this in constructor method
     * @param cacheManager
     */
    protected void initCache(ConnCacheManager cacheManager, final Configuration conf){
        String prefix = this.getClass().getSimpleName();
        storedCache = cacheManager.buildCache(prefix + CONN_CACHE_STORED, new CacheLoader<String, ConnectionContext>() {
            @Override
            public ConnectionContext load(String key) throws Exception {
                DataSource ds = getDatasourceById(key);
                return buildConnectionContext(ds, conf);
            }
        }, notification ->{
            assert notification.getValue() != null;
            close(notification.getValue().connection);
        });
        this.conf = conf;
        reqCache = cacheManager.buildCache(prefix + CONN_CACHE_REQ,notification ->{
            assert notification.getValue() != null;
            close(notification.getValue().connection);
        });
    }

    @Override
    public final List<String> getDatabases(String dsId) {
        return getConnAndRun(dsId, this::getDatabases0);
    }

    @Override
    public final List<String> getDatabases(DataSource dataSource) {
        if(dataSource.getId() > 0){
            return getDatabases(String.valueOf(dataSource.getId()));
        }
        return getConnAndRun(dataSource, this::getDatabases0);
    }

    @Override
    public final List<String> getTables(String dsId, String database) {
        return getConnAndRun(dsId, conn ->getTables0(conn, database));
    }

    @Override
    public final List<String> getTables(DataSource dataSource, String database) {
        if(dataSource.getId() > 0){
            return getTables(String.valueOf(dataSource.getId()), database);
        }
        return getConnAndRun(dataSource, conn -> getTables0(conn, database));
    }

    @Override
    public final MetaPartitionInfo getPartitions(String dsId, String database, String table) {
        return getConnAndRun(dsId, conn -> getPartitions0(conn, database, table));
    }

    @Override
    public final MetaPartitionInfo getPartitions(DataSource dataSource, String database, String table) {
        if(dataSource.getId() > 0){
            return getPartitions(String.valueOf(dataSource.getId()), database, table);
        }
        return getConnAndRun(dataSource, conn -> getPartitions0(conn, database, table));
    }

    @Override
    public final List<MetaColumnInfo> getColumns(String dsId, String database, String table) {
        return getConnAndRun(dsId, conn -> getColumns0(conn, database, table));
    }

    @Override
    public final List<MetaColumnInfo> getColumns(DataSource dataSource, String database, String table) {
        if(dataSource.getId() > 0){
            return getColumns(String.valueOf(dataSource.getId()), database, table);
        }
        return getConnAndRun(dataSource, conn -> getColumns0(conn, database, table));
    }

    /**
     * Get data source object by id
     * @param dsId
     * @return
     */
    protected abstract DataSource getDatasourceById(String dsId);

    /**
     * Get connection
     * @param dataSource
     * @param keytab
     * @return
     * @throws Exception
     */
    protected abstract C getDBConnection(DataSource dataSource, File keytab);

    /**
     * Get connection
     * @param dataSource
     * @return
     * @throws Exception
     */
    protected abstract C getDBConnection(DataSource dataSource);
    /**
     * Get database list by connection
     * @param connection
     * @return
     */
    public abstract List<String> getDatabases0(C connection);

    /**
     * Get table list by connection and database
     * @param connection cc
     * @param database
     * @return
     */
    public abstract List<String> getTables0(C connection, String database);

    /**
     * Get partitions by connection, database and table
     * @param connection
     * @param database
     * @param table
     * @return
     */
    public abstract MetaPartitionInfo getPartitions0(C connection, String database, String table);

    /**
     * Get columns by connection, database and table
     * @param connection
     * @param database
     * @param table
     * @return
     */
    public abstract List<MetaColumnInfo> getColumns0(C connection, String database, String table);

    protected void close(C connection){

    }

    protected <R>R getConnAndRun(String dsId, Function<C, R> action){
        try{
            ConnectionContext ctx;
            if(null != storedCache){
                ctx = storedCache.get(dsId);
            }else{
                ctx = buildConnectionContext(getDatasourceById(dsId), conf);
            }
            return run(ctx, action);
        }catch(EndPointException ei){
            throw ei;
        }catch(Exception e){
            if(e.getCause() instanceof EndPointException){
                throw (EndPointException)e.getCause();
            }
            logger.error("ERROR_IN_CACHE: message:" + e.getMessage(), e);
            reqCache.invalidate(dsId);
            throw new EndPointException("exchange.data_source.connect.error", e);
        }
    }

    protected <R>R getConnAndRun(DataSource ds, Function<C, R> action){
        String cacheKey = "";
        try{
            Map<String, Object> parameterMap = ds.resolveParams();
            String parameter = Json.toJson(parameterMap, null);
            assert parameter != null;
            cacheKey = CryptoUtils.md5(parameter, "", 2);
            ConnectionContext ctx;
            if(null != reqCache) {
                ctx = reqCache.get(cacheKey, () -> buildConnectionContext(ds, conf));
            }else{
                ctx = buildConnectionContext(ds, conf);
            }
            return run(ctx, action);
        }catch(EndPointException ei){
            throw ei;
        }catch(Exception e){
            if(e.getCause() instanceof EndPointException){
                throw (EndPointException)e.getCause();
            }
            logger.error("ERROR_IN_CACHE: message:" + e.getMessage(), e);
            reqCache.invalidate(cacheKey);
            throw new EndPointException("exchange.data_source.connect.error", e);
        }
    }
    private <R>R run(ConnectionContext ctx, Function<C, R> action){
        if(isConnLock()){
            ctx.lock.lock();
            try{
                return action.apply(ctx.connection);
            }finally{
                ctx.lock.unlock();
            }
        }else{
            return action.apply(ctx.connection);
        }
    }
    public  class ConnectionContext{
        /***
         * complicate when different threads calling connection API
         */
        private ReentrantLock lock = new ReentrantLock();

        private C connection;

        ConnectionContext(C connection){
            this.connection = connection;
        }
    }

    private ConnectionContext buildConnectionContext(DataSource ds, Configuration conf){
        Object v = ds.getParameterMap().get(Constants.PARAM_KERBEROS_BOOLEAN);
        if(null != v && TRUE.equals(String.valueOf(v))){
            String kbUri = String.valueOf(ds.getParameterMap().get(Constants.PARAM_KB_FILE_PATH));
            String path = null != conf?conf.getStoreTmp() + AppUtil.newFileName(kbUri) : TEMP_PATH + AppUtil.newFileName(kbUri);
            AppUtil.downloadFile(kbUri, path);
            try{
                //Sometimes when gets new config, it will close the old connection, like 'Hive'
                return new ConnectionContext(getDBConnection(ds, new File(path)));
            }finally{
                File file = new File(path);
                if(file.exists()){
                    if(!file.delete()){
                        logger.error("IO_ERROR: Delete downloaded file error, Path:" + file.getPath());
                    }
                }
            }
        }
        return new ConnectionContext(getDBConnection(ds));
    }
}
