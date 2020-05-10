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

package com.webank.wedatasphere.exchangis.gateway.auth;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication related method
 * @author davidhua
 * 2018/10/18
 */
@Controller
@RequestMapping("/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Resource
    private RouteLocator routeLocator;

    @Value("${sso.logoutUrl}")
    private String ssoLogoutUrl;
    /**
     * To destroy 'sso cookie'
     */
    @RequestMapping(value="/api/v1/logout", method = RequestMethod.POST)
    public ResponseEntity<Object> ssoLogout(){
        // To destroy SSO cookie(token)
        logger.info("Destroy the SSO token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Set-Cookie", AuthConstraints.DEFAULT_SSO_COOKIE+"=\"\"; Max-Age=0; Path=/; HTTPOnly");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> body = new HashMap<>(10);
        //Jump other url(Login page)
        body.put("redirect", ssoLogoutUrl);
        return ResponseEntity.ok().headers(httpHeaders)
                .body(Json.toJson(new Response<>().errorResponse(302, body, ""), null));
    }

}
