/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.dao.DataSourceModelDao;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author davidhua
 */
@Service
public class DataSourceModelServiceImpl extends AbstractGenericService<DataSourceModel> implements DataSourceModelService {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceModelServiceImpl.class);

    @Resource
    private DataSourceModelDao modelDao;

    @Override
    protected IBaseDao<DataSourceModel> getDao() {
        return modelDao;
    }

    @Override
    public boolean add(DataSourceModel modelAssembly) {
        return super.add(modelAssembly);
    }


    @Override
    public boolean exist(Integer id){
        return modelDao.selectOne(id) != null;
    }

    @Override
    public boolean update(DataSourceModel modelAssembly) {
        return super.update(modelAssembly);
    }

    @Override
    public long getCount(PageQuery pageQuery) {
        return super.getCount(pageQuery);
    }

    @Override
    public DataSourceModel get(Object id) {
        return super.get(id);
    }

    @Override
    public PageList<DataSourceModel> findPage(PageQuery pageQuery) {
        return super.findPage(pageQuery);
    }

    @Override
    public List<DataSourceModel> selectAllList(PageQuery pageQuery) {
        return super.selectAllList(pageQuery);
    }

    @Override
    protected DataSourceModel queryFilter(DataSourceModel templateStrucure) {
        return super.queryFilter(templateStrucure);
    }
}
