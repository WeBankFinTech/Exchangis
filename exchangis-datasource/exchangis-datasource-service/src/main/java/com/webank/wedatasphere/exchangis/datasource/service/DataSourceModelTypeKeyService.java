package com.webank.wedatasphere.exchangis.datasource.service;


import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModelTypeKey;

import java.util.List;

public interface DataSourceModelTypeKeyService {

    PageList<ExchangisDataSourceModelTypeKey> findDsModelTypeKeyPageList(DataSourceModelTypeKeyQuery pageQuery);

    List<ExchangisDataSourceModelTypeKey> selectAllDsModelTypeKeys(DataSourceModelTypeKeyQuery pageQuery);

    long countDsModelTypeKey();

}
