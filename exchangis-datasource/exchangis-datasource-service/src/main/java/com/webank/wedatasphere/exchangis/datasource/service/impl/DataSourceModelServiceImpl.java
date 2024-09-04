package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelation;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.exception.DataSourceModelOperateException;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelMapper;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelRelationMapper;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class DataSourceModelServiceImpl implements DataSourceModelService {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceModelServiceImpl.class);

    @Resource
    private DataSourceModelMapper dataSourceModelMapper;

    @Resource
    private DataSourceModelRelationMapper dataSourceModelRelationMapper;

    @Resource
    private RateLimitService rateLimitService;

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
    public boolean delete(Long id) throws DataSourceModelOperateException, RateLimitOperationException {
        List<Long> dsIds = dataSourceModelRelationMapper.queryDsIdsByModel(id);
        if (Objects.nonNull(dsIds) && dsIds.size() > 0) {
            throw new DataSourceModelOperateException("The model is in use, cannot delete it(模板正在被使用，请勿删除)");
        }
        // First to delete rate limit
        RateLimit query = new RateLimit();
        query.setLimitRealmId(id);
        RateLimit rateLimit = rateLimitService.selectOne(query);
        if (Objects.nonNull(rateLimit)) {
            rateLimitService.delete(rateLimit);
        }
        // Then to delete model
        return delete(Arrays.asList(id));
    }

    @Override
    @Transactional
    public boolean update(DataSourceModel dataSourceModel) throws DataSourceModelOperateException {
        Long id = dataSourceModel.getId();
        DataSourceModel queryModel = this.get(id);
        if (!queryModel.getModelName().equals(dataSourceModel.getModelName())
                && StringUtils.isNotBlank(dataSourceModel.getCreateUser())
                && isDuplicate(dataSourceModel.getModelName(), dataSourceModel.getCreateUser())) {
            throw new DataSourceModelOperateException("The model is in duplicate");
        }
        return dataSourceModelMapper.update(dataSourceModel) > 0;
    }

    /**
     *
     * @param modelId model id
     * @param update update vo
     * @return commit vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSourceModel beginUpdate(Long modelId, DataSourceModel update) throws DataSourceModelOperateException {
        // Update the model version
        int result = this.dataSourceModelMapper.updateVersion(modelId);
        if (result <= 0){
            throw new DataSourceModelOperateException("Fail to update version for model id: [" + modelId + "](更新模版版本失败)");
        }
        DataSourceModel before = this.dataSourceModelMapper.selectOne(modelId);
        // Create the duplicate model from major model
        before.setModelName(before.getModelName() + "_" + String.format("v%06d", before.getVersion()));
        before.setClusterName(update.getClusterName());
        before.setModelDesc(update.getModelDesc());
        before.setParameter(update.getParameter());
        before.setCreateUser(update.getModifyUser());
        before.setModifyUser(update.getModifyUser());
        before.setDuplicate(true);
        before.setRefModelId(modelId);
        //Empty id
        before.setId(null);
        try {
            this.dataSourceModelMapper.insert(before);
        } catch (DuplicateKeyException e){
            throw new DataSourceModelOperateException("The duplicate model name:[" + before.getModelName()
                    + "] meets conflicts (创建更新副本模版名冲突)");
        }
        return before;
    }

    /**
     *
     * @param modelId model id
     * @param commit duplicated model
     * @param update update model
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commitUpdate(Long modelId, DataSourceModel commit,
                             DataSourceModel update) throws DataSourceModelOperateException {
        update.setVersion(commit.getVersion());
        // Simply update the data source model
        int result = this.dataSourceModelMapper.updateInVersion(update);
        if (result <= 0){
            throw new DataSourceModelOperateException("Fail to update version for model id: [" + modelId + "](更新模版版本失败)");
        }
        this.dataSourceModelRelationMapper.deleteRelationByModelIds(Collections.singletonList(modelId));
        this.dataSourceModelRelationMapper.transferModelRelation(commit.getId(), modelId);
        this.dataSourceModelRelationMapper.deleteRefRelationByModelId(modelId);
        this.dataSourceModelMapper.deleteDuplicate(modelId);
    }

    @Override
    public long getCount(PageQuery pageQuery) {
        return dataSourceModelMapper.count(pageQuery);
    }

    @Override
    public PageResult<DataSourceModel> findPage(PageQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        PageHelper.startPage(currentPage, pageSize);
        try {
            List<DataSourceModel> data = dataSourceModelMapper.queryPageList(pageQuery);
            PageInfo<DataSourceModel> pageInfo = new PageInfo<>(data);
            return new PageResult<>(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public List<DataSourceModel> selectAllList(DataSourceModelQuery query) {
        return dataSourceModelMapper.selectAllList(query);
    }

    @Override
    public List<DataSourceModelRelation> queryRelations(Long id) {
        return this.dataSourceModelRelationMapper.queryRefRelationsByModel(id);
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
