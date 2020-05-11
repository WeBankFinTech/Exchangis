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
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.conns.ElasticSearch;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;
import com.webank.wedatasphere.exchangis.datasource.service.AbstractMetaDbService;
import com.webank.wedatasphere.exchangis.datasource.service.MetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;

/**
 * @author davidhua
 * 2019/8/20
 */
@Service(MetaService.PREFIX + "elasticsearch")
public class ElasticMetaDbServiceImpl extends AbstractMetaDbService<ElasticSearch.ElasticMeta> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticMetaDbServiceImpl.class);

    private static final String DEFAULT_TYPE_NAME = "type";
    @Resource
    private DataSourceServiceImpl dataSourceService;

    @Override
    protected DataSource getDatasourceById(String dsId) {
        DataSource ds = dataSourceService.getDetail(dsId);
        boolean match = null != ds;
        if(match && !ds.getSourceType().toLowerCase()
                .equals(TypeEnums.ELASTICSEARCH.v())){
            LOG.error("Data source id: " + dsId + ", type:" + ds.getSourceType() + ", is not elasticsearch");
            match = false;
        }
        if(!match){
            throw new EndPointException("exchange.elastic_meta.invalid.data_source.id",null,dsId);
        }
        ds.resolveParams();
        return ds;
    }

    @Override
    protected ElasticSearch.ElasticMeta getDBConnection(DataSource dataSource, File keytab) {
        try {
            Map<String, Object> parameters = dataSource.getParameterMap();
            String elasticUrls = String.valueOf(parameters.getOrDefault(PARAM_ES_URLS, ""));
            String[] endPoints = elasticUrls.split(DEFAULT_ENDPOINT_SPLIT);
            return ElasticSearch.buildClient(endPoints,
                    String.valueOf(parameters.getOrDefault(PARAM_DEFAULT_USERNAME, "")),
                    String.valueOf(CryptoUtils.string2Object(
                            String.valueOf(parameters.getOrDefault(PARAM_DEFAULT_PASSWORD, ""))))
                    );
        }catch(Exception e){
            LOG.error("Get ElasticSearch RestClient failed, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.elastic_meta.failed.to.connect", e);
        }
    }

    @Override
    protected ElasticSearch.ElasticMeta getDBConnection(DataSource dataSource) {
        return getDBConnection(dataSource, null);
    }

    @Override
    public List<String> getDatabases0(ElasticSearch.ElasticMeta connection) {
        //Get indices
        try{
            return connection.getAllIndices();
        }catch (Exception e){
            LOG.error("Get Elastic indices error, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.elastic_meta.failed.get.index_info", e);
        }
    }

    @Override
    public List<String> getTables0(ElasticSearch.ElasticMeta connection, String index) {
        //Get types
        try{
            return connection.getTypes(index);
        }catch (Exception e){
            LOG.error("Get Elastic types error, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.elastic_meta.failed.get.type_info", e,index);
        }
    }

    @Override
    public MetaPartitionInfo getPartitions0(ElasticSearch.ElasticMeta connection, String database, String table) {
        throw new EndPointException("exchange.elastic_meta.unsupported.request.interface", null);
    }

    @Override
    public List<MetaColumnInfo> getColumns0(ElasticSearch.ElasticMeta connection, String index, String type) {
        try {
            Map<Object, Object> props = connection.getProps(index, type);
            List<MetaColumnInfo> metaColumnInfoList = new ArrayList<>();
            resolveMetaColumn(metaColumnInfoList, null, props, ElasticSearch.DEFAULT_NAME_SPLIT);
            return metaColumnInfoList;
        } catch (Exception e) {
            LOG.error("Get Elastic columns error, message: " + e.getMessage(), e);
            throw new EndPointException("exchange.elastic_meta.failed.get.column", e,index,type);
        }
    }

    @Override
    protected void close(ElasticSearch.ElasticMeta connection) {
        try {
            connection.close();
        } catch (IOException e) {
            LOG.error("Close ElasticSearch connection failed, message: " + e.getMessage(), e);
        }
    }

    private void resolveMetaColumn(List<MetaColumnInfo> metaColumnInfoList,
                                   MetaColumnInfo column, Map<Object, Object> propsMap, String columnNameSeparator){
        propsMap.forEach((key, value) ->{
            if(value instanceof Map){
                Map metaMap = (Map)value;
                if(null != metaMap.get(DEFAULT_TYPE_NAME)){
                    MetaColumnInfo levelColumn = new MetaColumnInfo();
                    if(null != column) {
                        levelColumn.setName(column.getName() + columnNameSeparator + key);
                    }else{
                        levelColumn.setName(String.valueOf(key));
                    }
                    levelColumn.setType(String.valueOf(metaMap.get(DEFAULT_TYPE_NAME)));
                    metaColumnInfoList.add(levelColumn);
                }else if(null != metaMap.get(ElasticSearch.ElasticMeta.FIELD_PROPS)
                        && metaMap.get(ElasticSearch.ElasticMeta.FIELD_PROPS) instanceof Map){
                    MetaColumnInfo levelColumn = column;
                    if(null == levelColumn){
                        levelColumn = new MetaColumnInfo();
                        levelColumn.setName(String.valueOf(key));
                    }else{
                        levelColumn.setName(levelColumn.getName() + columnNameSeparator + key);
                    }
                    resolveMetaColumn(metaColumnInfoList, levelColumn, (Map)metaMap.get(ElasticSearch.ElasticMeta.FIELD_PROPS),
                            columnNameSeparator);
                }
            }
        });
    }
}
