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

import com.webank.wedatasphere.exchangis.auth.dao.UserExecNodeDao;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.Paginator;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeTabDao;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeUserDao;
import com.webank.wedatasphere.exchangis.exec.domain.*;
import com.webank.wedatasphere.exchangis.exec.query.ExecNodeUserQuery;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.exec.uid.UidGenerator;
import com.webank.wedatasphere.exchangis.executor.domain.ExecSysUser;
import com.webank.wedatasphere.exchangis.exec.remote.ExecuteService;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeDao;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.tab.dao.TabDao;
import com.webank.wedatasphere.exchangis.tab.domain.TabEntity;
import com.webank.wedatasphere.exchangis.exec.uid.PlatformUidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author davidhua
 * 2019/3/15
 */
@Service
public class ExecNodeInfoServiceImpl implements ExecNodeInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(ExecNodeInfoService.class);


    @Resource
    private ExecNodeDao execNodeDao;

    @Resource
    private UserExecNodeDao userExecNodeDao;

    @Resource
    private ExecNodeUserDao execNodeUserDao;

    @Resource
    private ExecuteService executeService;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private TabDao tabDao;

    @Resource
    private ExecNodeTabDao execNodeTabDao;

    @Override
    public boolean haveNodeIdsPermission(String appUser, List<Integer> execNodeIds) {
        execNodeIds = execNodeIds.stream().map(String::valueOf)
                .distinct()
                .map(Integer::valueOf).collect(Collectors.toList());
        List<ExecutorNode> nodes = userExecNodeDao.getExecNodeByUserAndIds(appUser, execNodeIds);
        List<Integer> nodeIds = nodes.stream().map(ExecutorNode::getId).distinct().collect(Collectors.toList());
        if(nodeIds.size() < execNodeIds.size()){
            List<Integer> copyExecNodeIds = new ArrayList<>(execNodeIds);
            copyExecNodeIds.removeAll(nodeIds);
            int defaultNodes = execNodeDao.countDefaultByIds(copyExecNodeIds);
            return defaultNodes == copyExecNodeIds.size();
        }
        return true;
    }

    @Override
    public Pair<List<ExecutorNode>, Boolean> haveNodeNamesPermission(String appUser, List<String> execNodeNames) {
        execNodeNames = execNodeNames.stream().distinct().collect(Collectors.toList());
        List<ExecutorNode> nodes = userExecNodeDao.getExecNodeByUserAndNames(appUser, execNodeNames);
        List<String> nodeNames = nodes.stream().map(ExecutorNode::getAddress).distinct().collect(Collectors.toList());
        if(nodeNames.size() < execNodeNames.size()){
            List<String> copyExecNodeNames = new ArrayList<>(execNodeNames);
            copyExecNodeNames.removeAll(nodeNames);
            int defaultNodes = execNodeDao.countDefaultByNames(copyExecNodeNames);
            return new MutablePair<>(nodes, defaultNodes == copyExecNodeNames.size());
        }
        return new MutablePair<>(nodes,true);
    }


    @Override
    public List<ExecutorNode> getExecNodeByUserAndNames(String appUser, List<String> execNodeNames) {
        return userExecNodeDao.getExecNodeByUserAndNames(appUser, execNodeNames);
    }

    @Override
    public List<ExecutorNode> getExecNodesByAppUser(String appUser) {
        return  userExecNodeDao.getExecNodeByUser(appUser);
    }

    @Override
    public List<ExecutorNode> getExecNodesByAppUserAndTab(String appUser, String tabName) {
        return userExecNodeDao.getExecNodeByUserAndTab(appUser, tabName);
    }

    @Override
    public ExecNodeUser getExecNodeUser(Integer nodeId, String execUser) {
        return execNodeUserDao.selectOne(nodeId, execUser);
    }

    @Override
    public int bindExecNodeAndUsers(ExecNodeUserBind bind) {
        Integer nodeId = bind.getNodeId();
        ExecutorNode node = execNodeDao.selectOne(nodeId);
        if(null != node && !bind.getExecUserList().isEmpty()) {
            String address = node.getAddress();
            List<ExecUser> execUserList =  bind.getExecUserList();
            ExecNodeUserBind.BindOpType opType = bind.getOpType();
            ExecNodeInfoService execNodeInfoService = (ExecNodeInfoService)AopContext.currentProxy();
            execNodeInfoService.bindExecNodeAndUsers(nodeId, execUserList);
            if(opType == ExecNodeUserBind.BindOpType.BIND_RELATE){
                AtomicInteger count = new AtomicInteger();
                execUserList.forEach(execUser -> {
                    try {
                        ExecNodeUser execNodeUser = execNodeUserDao.selectOne(nodeId, execUser.getExecUser());
                        if (execNodeInfoService.relateExecNodeAndUser(address, execNodeUser)) {
                            count.getAndIncrement();
                        }
                    }catch(Exception e){
                        LOG.error(e.getMessage(), e);
                    }
                });
                return count.get();
            }else{
                return execUserList.size();
            }
        }
        return 0;
    }

    @Override
    public int bindAndRelateExecNodesAndUser(List<Integer> nodeIds, List<String> nodeNames, ExecUser execUser) {
        List<ExecNodeUser> execNodeUsers = new ArrayList<>();
        nodeIds.forEach(nodeId -> {
            ExecNodeUser execNodeUser = new ExecNodeUser();
            execNodeUser.setExecNodeId(nodeId);
            execNodeUser.setExecUser(execUser.getExecUser());
            execNodeUser.setUpdateTime(Calendar.getInstance().getTime());
            execNodeUsers.add(execNodeUser);
        });
        execNodeUserDao.insertBatch(execNodeUsers);
        ExecNodeInfoService execNodeInfoService = (ExecNodeInfoService)AopContext.currentProxy();
        AtomicInteger count = new AtomicInteger();
        for(int i = 0 ; i < nodeIds.size(); i ++){
            Integer nodeId = nodeIds.get(i);
            String nodeName = nodeNames.size() > i ? nodeNames.get(i) : "";
            try {
                ExecNodeUser execNodeUser = execNodeUserDao.selectOne(nodeId, execUser.getExecUser());
                if (execNodeInfoService.relateExecNodeAndUser(nodeName, execNodeUser)) {
                    count.getAndIncrement();
                }
            }catch(Exception e){
                LOG.error(e.getMessage(), e);
            }
        }
        return count.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindExecNodeAndUsers(Integer nodeId, List<ExecUser> execUsers) {
        List<ExecNodeUser> execNodeUsers = new ArrayList<>();
        execUsers.forEach(execUser -> {
            ExecNodeUser execNodeUser = new ExecNodeUser();
            execNodeUser.setExecNodeId(nodeId);
            execNodeUser.setExecUser(execUser.getExecUser());
            execNodeUser.setUpdateTime(Calendar.getInstance().getTime());
            execNodeUsers.add(execNodeUser);
        });
        execNodeUserDao.insertBatch(execNodeUsers);
    }

    @Override
    public void unBindExecNodeAndUser(ExecNodeUser execNodeUser) {
        ExecutorNode node = execNodeDao.selectOne(execNodeUser.getExecNodeId());
        String address = null == node?null : node.getAddress();
        if(StringUtils.isNotBlank(address)){
            if(execNodeUser.getUid() != null) {
                int result = execNodeUserDao.markDelete(execNodeUser.getExecNodeId(), execNodeUser.getExecUser());
                if (result > 0) {
                    try {
                        Response<String> response = executeService.deleteSysUser(address, execNodeUser.getExecUser());
                        if (response.getCode() != 0) {
                            LOG.info("Fail to delete system user in executor, message: " + response.getMessage());
                        }
                    } catch (Exception e) {
                        LOG.error("Error to delete system user in executor", e);
                    }
                }
            }else{
                execNodeUserDao.delete(execNodeUser.getExecNodeId(), execNodeUser.getExecUser());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean relateExecNodeAndUser(String address, ExecNodeUser execNodeUser) {
        boolean result = false;
        if(StringUtils.isBlank(address)){
            ExecutorNode node = execNodeDao.selectOne(execNodeUser.getExecNodeId());
            address = null == node?address : node.getAddress();
        }
        if(StringUtils.isNotBlank(address)){
            String execUser = execNodeUser.getExecUser();
            Long uid = (long) (null == execNodeUser.getUid() ? -1 : execNodeUser.getUid());
            String userType = execNodeUser.getUserType();
            if(uid < 0){
                Pair<String, Long> pair = uidGenerator.generate(execNodeUser.getExecNodeId(), execUser);
                userType = pair.getLeft();
                uid = pair.getRight();
            }
            try {
                Response<ExecSysUser> response = executeService
                        .createSysUser(address, execUser, Math.toIntExact(uid), PlatformUidGenerator.DEFAULT_EXEC_USER_GROUP_ID);
                if (response.getCode() == 0) {
                    ExecSysUser sysUser = response.getData();
                    execNodeUser.setUserType(userType);
                    execNodeUser.setUid(sysUser.getUid());
                    execNodeUser.setGid(sysUser.getGid());
                    execNodeUser.setRelationState(1);
                    result = true;
                } else {
                    execNodeUser.setRelationState(2);
                    LOG.error("Fail to associate executive user: " + execUser + ", response msg:" + response.getMessage());
                }
            }catch(Exception e){
                execNodeUser.setRelationState(2);
                LOG.error("Fail to associate executive user: " + execUser + ", error msg:" + e.getMessage());
            }
            execNodeUserDao.updateOne(execNodeUser);
        }
        return result;
    }

    @Override
    public boolean notRelateExecNodeAndUser(ExecNodeUser execNodeUser) {
        execNodeUser.setRelationState(0);
        execNodeUserDao.updateOne(execNodeUser);
        return true;
    }

    @Override
    public PageList<ExecNodeUser> findExecNodeUserPage(ExecNodeUserQuery query) {
        Long count = execNodeUserDao.count(query);
        int currentPage = query.getPage();
        int pageSize = query.getPageSize();
        PageList<ExecNodeUser> page = new PageList<>(new Paginator(currentPage, pageSize, count.intValue()));
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<ExecNodeUser> result = execNodeUserDao.findPage(query, new RowBounds(offset, pageSize));
        page.setData(result);
        return page;
    }

    @Override
    public ExecutorNode selectExecNode(Integer nodeId) {
        return execNodeDao.selectOne(nodeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void attachTab(ExecNodeTabRelation relation) {
        List<Integer> tabIds = relation.getTabIds();
        if(tabIds.isEmpty() && !relation.getTabNames().isEmpty()) {
            tabIds = tabDao.getTabIdsByNameList(relation.getTabNames());
        }
        if(!tabIds.isEmpty()){
            List<Integer> oldTabIds = execNodeTabDao.getTabIdsByExecNode(relation.getNodeId());
            List<Integer> removeTabIds = new ArrayList<>(oldTabIds);
            removeTabIds.removeAll(tabIds);
            if (!removeTabIds.isEmpty()) {
                //delete
                execNodeTabDao.deleteBatch(relation.getNodeId(), removeTabIds);
            }
            tabIds.removeAll(oldTabIds);
            if (!tabIds.isEmpty()) {
                //add
                List<TabEntity> tabEntities = tabDao.getTabsByIds(tabIds);
                List<ExecNodeTab> attachTabs = new ArrayList<>();
                tabEntities.forEach(tabEntity -> attachTabs.add(new ExecNodeTab(relation.getNodeId(), tabEntity.getId(), tabEntity.getName())));
                execNodeTabDao.insertBatch(attachTabs);
            }
        }
    }

    @Override
    public List<ExecNodeUser> getExecNodeUserList(List<Integer> execNodes, String execUser) {
        return execNodeUserDao.listByExecNodeIdsAndUser(execNodes, execUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(Long id) {
        ExecutorNode node = execNodeDao.selectOne(id);
        //Delete relation of node and executive user
        execNodeUserDao.deleteByNodeId(node.getId());
        //Delete relation of node and app user
        userExecNodeDao.deleteByNodeId(node.getId());
        execNodeDao.delete(Collections.singletonList(id));
    }

    @Override
    public boolean changeDefault(Integer nodeId, boolean value) {
        return execNodeDao.updateDefault(nodeId, value) > 0;
    }

    @Override
    public List<ExecutorNode> getDefaultNodeListByTab(String tabName) {
        return execNodeDao.getDefaultNodesByTab(tabName);
    }

}
