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

package com.webank.wedatasphere.exchangis.external;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author davidhua
 * 2019/9/3
 */
@RequestMapping("/api/v1/ext/inform")
public class ExtInformController {

    @RequestMapping("mask")
    public Response<String> maskInform(){
        return null;
    }
}
