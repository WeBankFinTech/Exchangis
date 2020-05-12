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

package com.webank.wedatasphere.exchangis.exec.controller;

import com.webank.wedatasphere.exchangis.common.auth.annotations.ContainerAPI;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.SecurityUtil;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.group.service.UserGroupService;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.exec.service.impl.ExecNodeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author devendeng
 * @date 2018/8/24
 */
@RestController
@RequestMapping("/api/v1/exec/node")
public class ExecNodeController extends ExceptionResolverContext {
    private static final Logger logger = LoggerFactory.getLogger(ExecNodeController.class);
    @Resource
    private ExecNodeServiceImpl executorNodeService;
    @Resource
    private ExecNodeInfoService execNodeInfoService;
    @Resource
    private SecurityUtil util;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private JobTaskService jobTaskService;

    /**
     * Register execution node
     * @param node
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ContainerAPI
    public Response<String> register(@Valid @RequestBody ExecutorNode node){
        boolean status  = false;
        ExecutorNode nodeInDb = executorNodeService.getByAddress(node.getAddress());
        if(nodeInDb == null){
            //The execution node does not exist
            if(executorNodeService.add(node)){
                logger.info("Add executor node success {}", node.getAddress());
                status = true;
            }else{
                logger.info("Add executor node failed");
            }
        }else{
            if(executorNodeService.update(node)){
                status = true;
            }
        }
        return status? new Response<String>().successResponse(null) : new Response<String>().errorResponse(501,null,"register error");
    }

    /**
     * Heart beat
     * @param node
     * @return
     */
    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    @ContainerAPI
    public Response<String> heartbeat(@Valid @RequestBody ExecutorNode node){
        boolean status  = false;
        ExecutorNode nodeInDb = executorNodeService.getByAddress(node.getAddress());
        if(nodeInDb != null){
            if(executorNodeService.updateHeartbeat(node) > 0){
                status = true;
            }
            logger.info("Node information: " + Json.toJson(node, null));
            List<TaskState> taskStates = node.getTaskStates();
            //Update task state in database
            if(!taskStates.isEmpty()) {
                jobTaskService.updateTaskState(node.getAddress(), taskStates);
            }
        }else {
            executorNodeService.add(node);
            logger.info("Address [{}] not exists. so add one",node.getAddress());
            status = true;
        }
        return status? new Response<String>().successResponse(null) : new Response<String>().errorResponse(501,null,"register error");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response<Object> list(HttpServletRequest request){
        String userName = util.getUserName(request);
        Set<ExecutorNode> nodes = new HashSet<>(execNodeInfoService.getExecNodesByAppUser(userName));
        //Get default nodes
        List<ExecutorNode> defaultNodes = execNodeInfoService.getDefaultNodeListByTab(null);
        nodes.addAll(defaultNodes);
        return new Response<>().successResponse(nodes);
    }


    @RequestMapping(value= "/list/{tab:[.\\d\\w_-]+}", method = RequestMethod.GET)
    public Response<Object> listByTab(HttpServletRequest request, @PathVariable("tab")String tab){
        String userName = util.getUserName(request);
        Set<ExecutorNode> nodes = new HashSet<>(execNodeInfoService.getExecNodesByAppUserAndTab(userName, tab));
        //Get default nodes
        List<ExecutorNode> defaultNodes = execNodeInfoService.getDefaultNodeListByTab(tab);
        nodes.addAll(defaultNodes);
        return new Response<>().successResponse(nodes);
    }

}
