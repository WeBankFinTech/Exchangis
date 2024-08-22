package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;

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
    Map<String, Object> create(String operator, DataSourceCreateVo vo) throws ExchangisDataSourceException, JsonErrorException;

    /**
     * Update data source
     * @param operator operator
     * @param vo create vo
     * @return data source version parameters
     * @throws ExchangisDataSourceException e
     */
    Map<String, Object> update(String operator, Long id, DataSourceCreateVo vo) throws ExchangisDataSourceException;
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
