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

package com.webank.wedatasphere.exchangis.exec.service.impl;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeDao;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeTabRelation;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by devendeng on 2018/8/24.
 * @author devendeng
 */
@Service
public class ExecNodeServiceImpl extends AbstractGenericService<ExecutorNode> implements ExecNodeService {


    @Resource
    private ExecNodeDao execNodeDao;

    @Resource
    private ExecNodeInfoService execNodeInfoService;
    @Override
    protected IBaseDao<ExecutorNode> getDao() {
        return execNodeDao;
    }

    @Override
    public int updateHeartbeat(ExecutorNode node) {
        return execNodeDao.updateHeartbeat(node);
    }

    @Override
    public ExecutorNode getByAddress(String address) {
        return execNodeDao.getByAddress(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(ExecutorNode executorNode) {
        boolean result = super.add(executorNode);
        if(result && !executorNode.getTabNames().isEmpty()){
            ExecNodeTabRelation relation = new ExecNodeTabRelation();
            relation.setNodeId(executorNode.getId());
            relation.setTabNames(executorNode.getTabNames());
            execNodeInfoService.attachTab(relation);
        }
        return result;
    }
}
