package com.webank.wedatasphere.exchangis.datasource.service;


import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;

import java.util.List;
import java.util.Map;

public interface DataSourceModelTypeKeyService {

    PageList<DataSourceModelTypeKey> findDsModelTypeKeyPageList(DataSourceModelTypeKeyQuery pageQuery);

    List<Map<String, Object>> queryDsModelTypeKeys(String operator, DataSourceModelTypeKeyQuery pageQuery) throws ExchangisDataSourceException;

    long countDsModelTypeKey();

}
