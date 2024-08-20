package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModel;
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

/**
 * @author jefftlin
 * @date 2024/8/15
 */
@Service
public class DataSourceModelServiceImpl implements DataSourceModelService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelServiceImpl.class);

    @Resource
    private DataSourceModelMapper dataSourceModelMapper;


    @Override
    public boolean add(ExchangisDataSourceModel dataSourceModel) {
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
    public boolean update(ExchangisDataSourceModel dataSourceModel) {
        return dataSourceModelMapper.update(dataSourceModel) > 0;
    }

    @Override
    public long getCount(PageQuery pageQuery) {
        return dataSourceModelMapper.count(pageQuery);
    }

    @Override
    public List<ExchangisDataSourceModel> findPage(PageQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<ExchangisDataSourceModel> result = dataSourceModelMapper.findPage(pageQuery, new RowBounds(offset, pageSize));
        return result;
    }

    @Override
    public List<ExchangisDataSourceModel> selectAllList(PageQuery pageQuery) {
        return null;
    }

    @Override
    public ExchangisDataSourceModel get(Long id) {
        return dataSourceModelMapper.selectOne(id);
    }

    @Override
    public boolean exist(Long id) {
        return Objects.nonNull(dataSourceModelMapper.selectOne(id));
    }

    @Override
    public List<ExchangisDataSourceModel> queryWithRateLimit() {
        return dataSourceModelMapper.queryWithRateLimit();
    }
}
