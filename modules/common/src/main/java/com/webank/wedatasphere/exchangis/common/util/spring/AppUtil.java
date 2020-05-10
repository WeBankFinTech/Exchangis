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

package com.webank.wedatasphere.exchangis.common.util.spring;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.UUID;

/**
 * @author davidhua
 * 2018/9/4
 */
@Component
public class AppUtil implements ApplicationContextAware,ApplicationListener<WebServerInitializedEvent> {

    private static final String LOCAL_ADDR = "127.0.0.1";

    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);

    private static String port;

    private static final String HTTP_HEADER = "http://";

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext app) throws BeansException {
        applicationContext = app;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        port = String.valueOf(webServerInitializedEvent.getWebServer().getPort());
    }

    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }

    /**
     * get the Ip and port for Application
     * @return http://{ip}:{port}
     */

    public static String getIpAndPort(){
        String ip = null;
        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
        }
        return String.format("http://%s:%s", ip, port);
    }

    public static String newFileName(String originName){
        String newName = UUID.randomUUID().toString().replace("-", "");
        if(StringUtils.isNotBlank(originName)){
            int index = originName.lastIndexOf(".");
            if(index >= 0){
                newName += originName.substring(index);
            }
        }
        return newName;
    }

    /**
     * remove http file
     * @param source
     */
    public static void removeFile(String source){
        HttpURLConnection connection = null;
        try{
            if(source.startsWith(HTTP_HEADER)){
                String ip;
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    ip = LOCAL_ADDR;
                }
                if(source.substring(HTTP_HEADER.length()).startsWith(ip)) {
                    source = source.replace(ip, LOCAL_ADDR);
                }
                connection = (HttpURLConnection)new URL(source).openConnection();
                connection.addRequestProperty("Cookie",AuthConstraints.DEFAULT_SSO_COOKIE + "="
                        + System.getProperty(AuthConstraints.ENV_SERV_TOKEN));
                connection.setRequestMethod("DELETE");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoOutput(true);
                connection.connect();
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }finally{
            if(null != connection){
                connection.disconnect();
            }
        }
    }
    /**
     * Download File
     * @param source
     * @param dst
     */
    public static void downloadFile(String source, String dst){
        FileOutputStream out = null;
        InputStream in = null;
        try {
            out = new FileOutputStream(dst);
            if(source.startsWith(HTTP_HEADER)){
                String ip;
                try {
                     ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    ip = LOCAL_ADDR;
                }
                if(source.substring(HTTP_HEADER.length()).startsWith(ip)) {
                    source = source.replace(ip, LOCAL_ADDR);
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection)new URL(source).openConnection();
                    conn.addRequestProperty("Cookie",AuthConstraints.DEFAULT_SSO_COOKIE + "="
                            + System.getProperty(AuthConstraints.ENV_SERV_TOKEN));
                    logger.info("Cookie: " + AuthConstraints.DEFAULT_SSO_COOKIE + "="
                            + System.getProperty(AuthConstraints.ENV_SERV_TOKEN));
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(5000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    in = conn.getInputStream();
                    IOUtils.copy(in, out);
                } finally{
                    if(null != conn){
                        conn.disconnect();
                    }
                }
            }else{
                try {
                    in = new FileInputStream(dst);
                    IOUtils.copy(in, out);
                }finally {
                    if(null != in){
                        in.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            if(null != out){
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Get cookie value
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name) && StringUtils.isNotBlank(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static boolean isAjax(HttpServletRequest request){
        return request.getHeader("X-Requested-With") != null &&
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

}
