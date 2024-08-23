package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKeyQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelTypeKey;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelTypeKeyMapper;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelTypeKeyService;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DataSourceModelTypeKeyServiceImpl implements DataSourceModelTypeKeyService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelTypeKeyServiceImpl.class);

    @Resource
    private DataSourceModelTypeKeyMapper dataSourceModelTypeKeyMapper;

    @Override
    public PageList<DataSourceModelTypeKey> findDsModelTypeKeyPageList(DataSourceModelTypeKeyQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        PageList<DataSourceModelTypeKey> page = new PageList<>(currentPage, pageSize, offset);
        List<DataSourceModelTypeKey> data = dataSourceModelTypeKeyMapper.findPage(pageQuery, new RowBounds(offset, pageSize));
        page.setData(data);
        return page;
    }

    @Override
    public List<DataSourceModelTypeKey> selectAllDsModelTypeKeys(DataSourceModelTypeKeyQuery pageQuery) {
        return dataSourceModelTypeKeyMapper.queryList(pageQuery);
    }

    @Override
    public long countDsModelTypeKey() {
        return dataSourceModelTypeKeyMapper.count();
    }
}
