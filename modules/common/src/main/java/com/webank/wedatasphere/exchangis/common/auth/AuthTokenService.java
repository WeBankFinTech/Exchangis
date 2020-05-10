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

package com.webank.wedatasphere.exchangis.common.auth;

import com.webank.wedatasphere.exchangis.route.feign.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author davidhua
 * 2019/4/9
 */
@FeignClient(name = "exchangis-gateway")
public interface AuthTokenService {
    /**
     * refresh token used by normal user
     * @param refresh
     * @param token
     * @return
     */
    @GetMapping("/token/refresh")
    String refreshToken(@RequestHeader(FeignConstants.HOST_PORT)String hostPort, @RequestParam(AuthConstraints.TICKT_REFRESH_PARAM)boolean refresh,
                        @RequestParam(AuthConstraints.DEFAULT_SSO_COOKIE)String token);

    @GetMapping("/token/refresh")
    String refreshToken(@RequestParam(AuthConstraints.TICKT_REFRESH_PARAM)boolean refresh,
                        @RequestParam(AuthConstraints.DEFAULT_SSO_COOKIE)String token);
    /**
     * refresh token used  by containers
     * @param id
     * @param pwd
     * @return
     */
    @PostMapping("/token/serv/refresh")
    String refreshServerToken(@RequestParam(AuthConstraints.TOKEN_REFRESH_ID)String id,
                              @RequestParam(AuthConstraints.TOKEN_REFRESH_PWD)String pwd);
}
