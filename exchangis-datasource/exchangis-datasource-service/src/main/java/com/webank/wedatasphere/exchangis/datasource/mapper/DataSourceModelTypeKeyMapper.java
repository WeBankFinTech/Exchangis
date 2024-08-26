package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
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
    List<DataSourceModelTypeKey> findPage(DataSourceModelTypeKeyQuery pageQuery, RowBounds rowBound);

    /**
     * Query list in condition
     * @return list
     */
    List<DataSourceModelTypeKey> queryList(DataSourceModelTypeKeyQuery pageQuery);
}
