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

package com.webank.wedatasphere.exchangis.common.auth.interceptors;

import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenService;
import com.webank.wedatasphere.exchangis.common.auth.exceptions.ExpireTimeOutException;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.webank.wedatasphere.exchangis.common.auth.AuthConstraints.SSO_REDIRECT_SERVICE;
import static com.webank.wedatasphere.exchangis.common.auth.AuthConstraints.SSO_REDIRECT_SYSTEMID;

/**
 * @author davidhua
 * 2018/10/16
 */
@Component
@ConditionalOnClass(HandlerInterceptor.class)
public class AuthenticationInterceptor implements HandlerInterceptor{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private static final String PROTOCOL_HEADER = "http";
    @Resource
    private AuthConfiguration conf;

    @Resource
    private AuthTokenHelper authTokenHelper;

    private AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(conf.enable() && !request.getRequestURI().equals(conf.authLoginUrl())){
            if(null == authTokenService){
                authTokenService = AppUtil.getBean(AuthTokenService.class);
            }
            String token = AppUtil.getCookieValue(request, AuthConstraints.DEFAULT_SSO_COOKIE);
            if(StringUtils.isBlank(token)) {
                LOGGER.info("cannot find token, sendRedirect..., request path: {}", request.getRequestURI());
                sendRedirect(request, response);
                return false;
            }
            boolean ok = false;
            boolean fresh = false;
            try {
                ok = authTokenHelper.validate(token, conf.tokenInvalid());
            }catch(ExpireTimeOutException e){//should refresh the token by ticket
                token = refreshToken(token);
                LOGGER.info("trigger Refresh the token by ticket");
                fresh = true;
                ok = authTokenHelper.validate(token, conf.tokenInvalid());
                LOGGER.info("finish Refresh the token");
            }
            if (!ok) {
                LOGGER.info("validate auth token failed, sendRedirect...,  request path: {}", request.getRequestURI());
                sendRedirect(request, response);
            }else if(fresh){
                Cookie cookie = new Cookie(AuthConstraints.DEFAULT_SSO_COOKIE, token);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setMaxAge(86400);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            return ok;
        }
        return true;
    }

    private void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectBuilder = new StringBuilder();
        String loginUrl = conf.authLoginUrl();
        redirectBuilder.append(loginUrl);
        if(conf.authCasSwitch()) {
            //Append CAS parameters
            redirectBuilder.append("?")
                    .append(SSO_REDIRECT_SERVICE + "=").append(URLEncoder.encode(conf.gatewayUrl(), "UTF-8"));
            if (StringUtils.isNotBlank(conf.casSystemId())) {
                redirectBuilder.append("&" + SSO_REDIRECT_SYSTEMID + "=").append(conf.casSystemId());
            }
        }
        //Jump to gateway url
        sendRedirect(request, response, redirectBuilder.toString());
    }
    private void sendRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws IOException {
        if (AppUtil.isAjax(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            Response<Object> resp = new Response<>();
            resp.setCode(302);
            Map<String, Object> map = new HashMap<>(16);
            map.put("redirect", redirectUrl);
            resp.setData(map);
            final PrintWriter writer = response.getWriter();
            writer.write(Objects.requireNonNull(Json.toJson(resp, Response.class)));
            writer.flush();
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

    /**
     * To reduce the complex of web page, do that in backend
     */
    private String refreshToken(String token){
        if(null != authTokenService) {
            try {
                //get gateway address from token
                if(conf.authCasSwitch()) {
                    String hostPort = authTokenHelper.getAuthHeader(token)
                            .getOrDefault(AuthConstraints.HOST_PORT, "");
                    if (StringUtils.isNotBlank(hostPort)) {
                        LOGGER.info("Refresh token, gateway address： " + hostPort);
                        return authTokenService.refreshToken(hostPort, true, token);
                    }
                }else{
                    LOGGER.info("Refresh token to gateway in eureka ");
                    return authTokenService.refreshToken(false, token);
                }
            } catch (Exception e) {
                LOGGER.info("Fail to refresh token: [" + e.getMessage() + "]");
            }
        }
        return null;
    }
}
