package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.exception.DataSourceModelOperateException;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelMapper;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelRelationMapper;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DataSourceModelServiceImpl implements DataSourceModelService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelServiceImpl.class);

    @Resource
    private DataSourceModelMapper dataSourceModelMapper;

    @Resource
    private DataSourceModelRelationMapper dataSourceModelRelationMapper;

    @Override
    public boolean add(DataSourceModel dataSourceModel) {
        return dataSourceModelMapper.insert(dataSourceModel) > 0;
    }

    @Override
    public boolean delete(List<Object> ids) {
        return dataSourceModelMapper.delete(ids) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        List<Long> dsIds = dataSourceModelRelationMapper.queryDsIdsByModel(id);
        if (Objects.nonNull(dsIds) && dsIds.size() > 0) {
            throw new DataSourceModelOperateException("The model is in use, cannot delete it");
        }
        LOG.info("Delete datasource id : {}", id);
        return delete(Arrays.asList(id));
    }

    @Override
    @Transactional
    public boolean update(DataSourceModel dataSourceModel) {
        Long id = dataSourceModel.getId();
        DataSourceModel queryModel = this.get(id);
        if (!queryModel.getModelName().equals(dataSourceModel.getModelName())
                && StringUtils.isNotBlank(dataSourceModel.getCreateUser())
                && isDuplicate(dataSourceModel.getModelName(), dataSourceModel.getCreateUser())) {
            throw new DataSourceModelOperateException("The model is in duplicate");
        }
        return dataSourceModelMapper.update(dataSourceModel) > 0;
    }

    @Override
    public long getCount(PageQuery pageQuery) {
        return dataSourceModelMapper.count(pageQuery);
    }

    @Override
    public PageList<DataSourceModel> findPage(PageQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        PageList<DataSourceModel> page = new PageList<>(currentPage, pageSize, offset);
        List<DataSourceModel> data = dataSourceModelMapper.findPage(pageQuery, new RowBounds(offset, pageSize));
        page.setData(data);
        return page;
    }

    @Override
    public List<DataSourceModel> selectAllList(DataSourceModelQuery query) {
        return dataSourceModelMapper.selectAllList(query);
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

    private boolean isDuplicate(String tsName, String createUser) {
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelExactName(tsName);
        query.setCreateUser(createUser);
        return !this.selectAllList(query).isEmpty();
    }

}
