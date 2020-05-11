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

package com.webank.wedatasphere.exchangis.executor.util;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author davidhua
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CredentialsProvider provider;

    private CloseableHttpClient httpClient;

    private static HttpClientUtil clientUtil;

    private static int HTTP_TIMEOUT_IN_MILLISECONDS = 5000;

    private static final int POOL_SIZE = 20;

    public static void setHttpTimeoutInMillionSeconds(int httpTimeoutInMillionSeconds) {
        HTTP_TIMEOUT_IN_MILLISECONDS = httpTimeoutInMillionSeconds;
    }

    public static synchronized HttpClientUtil getHttpClientUtil() {
        if (null == clientUtil) {
            synchronized (HttpClientUtil.class) {
                if (null == clientUtil) {
                    clientUtil = new HttpClientUtil();
                }
            }
        }
        return clientUtil;
    }

    private HttpClientUtil() {
        initApacheHttpClient();
    }

    public void destroy() {
        destroyApacheHttpClient();
    }

    private void initApacheHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HTTP_TIMEOUT_IN_MILLISECONDS)
                .setConnectTimeout(HTTP_TIMEOUT_IN_MILLISECONDS).setConnectionRequestTimeout(HTTP_TIMEOUT_IN_MILLISECONDS)
                .setStaleConnectionCheckEnabled(true).build();

        if (null == provider) {
            httpClient = HttpClientBuilder.create().setMaxConnTotal(POOL_SIZE).setMaxConnPerRoute(POOL_SIZE)
                    .setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClientBuilder.create().setMaxConnTotal(POOL_SIZE).setMaxConnPerRoute(POOL_SIZE)
                    .setDefaultRequestConfig(requestConfig).setDefaultCredentialsProvider(provider).build();
        }
    }

    private void destroyApacheHttpClient() {
        try {
            if (httpClient != null) {
                httpClient.close();
                httpClient = null;
            }
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public String downLoad(String url,String localPath) {
        OutputStream out = null;
        InputStream in = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            addTokenHeader(httpGet);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();

            long length = entity.getContentLength();
            if (length <= 0) {
                throw new RuntimeException("Download "+url+" file not exist.");
            }
            File file = new File(localPath);
            if(!file.exists()){
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength=in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Down file [" + url + "] exception.",e);
        }finally{
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                logger.warn("Close input stream exception.",e);
            }

            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                logger.warn("Close output stream exception.",e);
            }
        }
        return localPath;
    }

    private void addTokenHeader(HttpRequestBase requestBase) throws IOException {
        String token = System.getProperty(AuthConstraints.ENV_SERV_TOKEN);
        if(StringUtils.isBlank(token)){
            //try to read token from file
            String tokenPath = System.getProperty(AuthConstraints.ENV_SERV_TOKEN_PATH);
            if(StringUtils.isNotBlank(tokenPath)){
                token = readToken(tokenPath);
            }
        }
        requestBase.addHeader("Cookie",  AuthConstraints.DEFAULT_SSO_COOKIE + "=" + token);
    }

    private String readToken(String tokenPath) throws IOException {
        String token = null;
        File file = new File(tokenPath);
        if (file.exists()) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                StringBuilder buffer = new StringBuilder();
                String str;
                while((str = reader.readLine()) != null){
                    buffer.append(str);
                }
                token = buffer.toString();
            }
        }
        return token;
    }
}
