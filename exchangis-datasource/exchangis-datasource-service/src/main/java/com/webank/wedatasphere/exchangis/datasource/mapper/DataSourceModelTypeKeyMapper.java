package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModelTypeKey;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface DataSourceModelTypeKeyMapper {

    /**
     * Count result
     *
     * @return value
     */
    long count();

    /**
     * Search
     *
     * @return
     */
    List<ExchangisDataSourceModelTypeKey> findPage(DataSourceModelTypeKeyQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<ExchangisDataSourceModelTypeKey> selectAllList(DataSourceModelTypeKeyQuery pageQuery);
}
