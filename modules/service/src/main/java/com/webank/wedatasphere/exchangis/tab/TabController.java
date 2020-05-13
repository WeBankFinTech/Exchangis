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

package com.webank.wedatasphere.exchangis.tab;

import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.tab.domain.TabEntity;
import com.webank.wedatasphere.exchangis.tab.service.TabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author davidhua
 * 2019/12/29
 */

@RestController
@RequestMapping("/api/v1/tab")
public class TabController extends ExceptionResolverContext {
    private static final Logger logger = LoggerFactory.getLogger(TabController.class);

    @Resource
    private TabService tabService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response<Object> list(){
        List<TabEntity> tabEntityList = tabService.listAll();
        return new Response<>().successResponse(tabEntityList);
    }

}

