package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelMapper;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DataSourceModelServiceImpl implements DataSourceModelService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelServiceImpl.class);

    @Resource
    private DataSourceModelMapper dataSourceModelMapper;


    @Override
    public boolean add(DataSourceModel dataSourceModel) {
        return dataSourceModelMapper.insert(dataSourceModel) > 0;
    }

    @Override
    public boolean delete(List<Object> ids) {
        return dataSourceModelMapper.delete(ids) > 0;
    }

    @Override
    public boolean delete(String ids) {
        String[] idsStr = ids.split(",");
        List<Object> list = new ArrayList<Object>();
        for (String id : idsStr) {
            list.add(Long.valueOf(id));
        }
        LOG.info("Delete ids: {}", ids);
        return delete(list);
    }

    @Override
    public boolean update(DataSourceModel dataSourceModel) {
        return dataSourceModelMapper.update(dataSourceModel) > 0;
    }

    @Override
    public long getCount(PageQuery pageQuery) {
        return dataSourceModelMapper.count(pageQuery);
    }

    @Override
    public List<DataSourceModel> findPage(PageQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<DataSourceModel> result = dataSourceModelMapper.findPage(pageQuery, new RowBounds(offset, pageSize));
        return result;
    }

    @Override
    public List<DataSourceModel> selectAllList(PageQuery pageQuery) {
        return null;
    }

    @Override
    public DataSourceModel get(Long id) {
        return dataSourceModelMapper.selectOne(id);
    }

    @Override
    public boolean exist(Long id) {
        return Objects.nonNull(dataSourceModelMapper.selectOne(id));
    }

    @Override
    public List<DataSourceModel> queryWithRateLimit() {
        return dataSourceModelMapper.queryWithRateLimit();
    }
}
