package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceDetail;
import com.webank.wedatasphere.exchangis.datasource.remote.DataSourceDbTableColumn;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceItem;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceTypeDefinition;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVo;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;
import java.util.Map;

/**
 * Data source service api
 * TODO define all inf in <em>ExchangisDataSourceService</em>
 */
public interface DataSourceService {
    /**
     * Create data source
     * @param operator operator
     * @param vo create vo
     * @return data source version parameters
     */
    Map<String, Object> create(String operator, DataSourceCreateVo vo) throws ExchangisDataSourceException;

    /**
     * Update data source
     * @param operator operator
     * @param vo create vo
     * @return data source version parameters
     * @throws ExchangisDataSourceException e
     */
    Map<String, Object> update(String operator, Long id, DataSourceCreateVo vo) throws ExchangisDataSourceException;

    /**
     * Update from data source version and model
     * @param operator operator
     * @param id data source id
     * @param name data source name
     * @param version  version id
     * @param model model
     */
    void updateInVersionAndModel(String operator, Long id, String name,
                                 Long version, DataSourceModel model);
    /**
     * Delete data source
     * @param operator operator
     * @param id data source id
     * @return id
     * @throws ExchangisDataSourceException e
     */
    Long delete(String operator, Long id) throws ExchangisDataSourceException;
    /**
     * List data source types
     * @param operator operator
     * @param engineType engine type
     * @param direct direct
     * @param sourceType source type
     * @return
     */
    List<ExchangisDataSourceTypeDefinition> listDataSourceTypes(String operator,
                                                                String engineType, String direct, String sourceType) throws ExchangisDataSourceException;

    /**
     * Check if support data source
     * @param engine engine
     * @param sourceType  source type
     * @param sinkType sink type
     */
    void supportDataSource(String engine, String sourceType, String sinkType) throws ExchangisDataSourceException;

    /**
     * Get data source param key definition
     * @param operator operator
     * @param typeId type id
     * @return map
     */
    List<Map<String, Object>> getDataSourceKeyDefine(String operator, Long typeId) throws ExchangisDataSourceException;

    /**
     * Query data sources
     * @param operator operator
     * @return page result
     */
    PageResult<ExchangisDataSourceItem> queryDataSources(String operator, DataSourceQueryVo vo) throws ExchangisDataSourceException;

    /**
     * List data sources
     * @param operator operator
     * @param typeName type name
     * @param typeId type id
     * @param page page num
     * @param pageSize page size
     * @return
     */
    List<ExchangisDataSourceItem> listDataSources(String operator,
                                                  String typeName, Long typeId,
                                                  Integer page, Integer pageSize) throws ExchangisDataSourceException;

    /**
     * Get data source detail
     * @param operator operator
     * @param id id
     * @param versionId version id
     * @return detail
     */
    ExchangisDataSourceDetail getDataSource(String operator, Long id, String versionId) throws ExchangisDataSourceException;

    ExchangisDataSourceDetail getDataSource(String operator, Long id) throws ExchangisDataSourceException;

    /**
     * Get data source by name
     * @param operator operator
     * @param name data source name
     * @return detail
     */
    ExchangisDataSourceDetail getDataSource(String operator, String name) throws ExchangisDataSourceException;

    /**
     * Get version list of data source by id
     * @param operator operator
     * @param id data source id
     * @return versions
     */
    List<Map<String, Object>> getDataSourceVersionsById(String operator, Long id) throws ExchangisDataSourceException;

    /**
     * Get connect params
     * @param operator operator
     * @param id data source id
     * @return
     */
    Map<String, Object> getDataSourceConnectParamsById(String operator, Long id) throws ExchangisDataSourceException;
    /**
     * Test connect
     * @param operator operator
     * @param id data source id
     * @param version version id
     */
    void testConnect(String operator, Long id, Long version) throws ExchangisDataSourceException;

    /**
     * Test connect by value obj
     * @param operator operator
     * @param vo value object
     */
    void testConnectByVo(String operator, DataSourceCreateVo vo) throws ExchangisDataSourceException;

    /**
     * Publish data source
     * @param operator operator
     * @param id id
     * @param version version
     */
    void publishDataSource(String operator, Long id, Long version) throws ExchangisDataSourceException;

    /**
     * Expire data source
     * @param operator operator
     * @param id id
     */
    void expireDataSource(String operator, Long id) throws ExchangisDataSourceException;
    /**
     * Get databases from data source
     * @param operator operator
     * @param type type
     * @param id data source id
     * @return databases
     */
    List<String> getDatabases(String operator, String type, Long id) throws ExchangisDataSourceException;

    /**
     * Get tables in database from data source
     * @param operator operator
     * @param type type
     * @param id data source id
     * @param database database name
     * @return tables
     */
    List<String> getTables(String operator, String type, Long id, String database) throws ExchangisDataSourceException;

    /**
     * Get table fields or columns
     * @param operator operator
     * @param type type
     * @param id data source id
     * @param database database name
     * @param table table name
     * @return columns
     */
    List<DataSourceDbTableColumn> getTableFields(String operator, String type,
                                                 Long id, String database, String table) throws ExchangisDataSourceException;

    /**
     * Copy data source
     * @param operator operate user
     * @param sourceName source name
     * @param newName new name
     */
    void copyDataSource(String operator,
                        String sourceName, String newName) throws ErrorException;

    /**
     * Recycle data source
     */
    void recycleDataSource(String userName, String handover) throws ExchangisDataSourceException;

}
