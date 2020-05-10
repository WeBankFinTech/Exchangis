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
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenBean;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.auth.exceptions.ExpireTimeOutException;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.gateway.auth.cas.CasSessionStorage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author davidhua
 * 2018/10/30
 */
@RequestMapping("/token")
@Controller
public class TokenController {
    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);
    @Resource
    private AuthTokenHelper tokenBuilder;
    @Resource
    private WhiteList whiteList;

    @Resource
    private CasSessionStorage storage;

    @Value("${auth.token.session-in-minutes:30}")
    private Long sessionInMinutes;

    @RequestMapping("refresh")
    public ResponseEntity<String> refresh(@RequestParam(AuthConstraints.TICKT_REFRESH_PARAM)boolean ticketRefresh,
                                          @RequestParam(AuthConstraints.DEFAULT_SSO_COOKIE)String token){
        Map<String, String> claims = tokenBuilder.getAuthMessage(token);
        if(ticketRefresh) {
            String ticket = claims.getOrDefault(AuthConstraints.TICKET_NAME, "");
            if (StringUtils.isBlank(ticket) || !storage.exist(ticket)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }else{
            try {
                tokenBuilder.validate(token, sessionInMinutes);
            }catch(ExpireTimeOutException e){
                LOG.info("Token Session expired: " + token.substring(0, 6) +  "****");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        Map<String, String> headers = tokenBuilder.getAuthHeader(token);
        AuthTokenBean tokenBean = new AuthTokenBean();
        tokenBean.setClaims(claims);
        tokenBean.setHeaders(headers);
        try{
            String newToken = tokenBuilder.build(tokenBean);
            LOG.info("Refresh TOKEN: "  + newToken.substring(0, 6) + "****");
            return ResponseEntity.ok().body(newToken);
        } catch(UnsupportedEncodingException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @RequestMapping(value = "/serv/refresh", method = RequestMethod.POST)
    public ResponseEntity<String> servRefresh(@RequestParam(value = AuthConstraints.TOKEN_REFRESH_ID)String id,
                                        @RequestParam(value = AuthConstraints.TOKEN_REFRESH_PWD)String pwd){
        List<String> allowedUris = whiteList.getList(id, pwd, null);
        if(allowedUris.isEmpty()){
            return ResponseEntity.status(401).body(null);
        }
        AuthTokenBean tokenBean = new AuthTokenBean();
        Map<String, String> headers = tokenBean.getHeaders();
        headers.put(AuthConstraints.ALLOWEDURIS, Json.toJson(allowedUris, String.class));
        try {
            String token = tokenBuilder.build(tokenBean);
            LOG.info("Refresh SERVICE-TOKEN: " + token.substring(0, 6) + "****");
            return ResponseEntity.ok().body(token);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
