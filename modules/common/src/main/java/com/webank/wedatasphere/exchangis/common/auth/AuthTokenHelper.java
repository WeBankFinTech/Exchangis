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

import com.webank.wedatasphere.exchangis.common.auth.exceptions.ExpireTimeOutException;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author davidhua
 * 2018/10/16
 */
public class AuthTokenHelper {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenHelper.class);

    private static final String PART_SPLIT = ",";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int ONE_MINUTES = 60 * 1000;

    private static final int AUTH_TOKEN_LENGTH = 3;
    private String tokenSecret = "default";

    public AuthTokenHelper(){

    }

    public AuthTokenHelper(String tokenSecret){
        this.tokenSecret = tokenSecret;
    }

    /**
     * build
     * @param tokenBean
     * @return 'token'
     */
    public String build(AuthTokenBean tokenBean) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Map<String, String> headers = tokenBean.getHeaders();
        Map<String, String> claims = tokenBean.getClaims();
        SimpleDateFormat format = new SimpleDateFormat(AuthConstraints.SSO_TOKEN_TIME_PATTERN);
        String time = format.format(Calendar.getInstance().getTime());
        headers.put(AuthConstraints.SSO_TOKEN_TIMESTAMP, time);
        String alg = headers.get(AuthTokenBean.ALG_NAME);
        Base64.Encoder encoder = Base64.getEncoder();
        String self = encoder.encodeToString(Objects.requireNonNull(Json.toJson(headers, Map.class)).getBytes(DEFAULT_CHARSET))
                + PART_SPLIT + encoder.encodeToString(Objects.requireNonNull(Json.toJson(claims, Map.class)).getBytes(DEFAULT_CHARSET));
        builder.append(self).append(PART_SPLIT);
        AuthTokenBean.Type type = AuthTokenBean.Type.of(alg);
        String auth = getAuthString(type, tokenSecret +self, time);
        //headers
        builder.append(auth);
        return URLEncoder.encode(builder.toString(), DEFAULT_CHARSET);
    }
    public Map<String, String> getAuthMessage(String token){
        try {
            token = URLDecoder.decode(token, DEFAULT_CHARSET);
            String[] parts = token.split(PART_SPLIT);
            if(parts.length != AUTH_TOKEN_LENGTH){
                throw new RuntimeException("invalid length in token : " + token);
            }
            return Json.fromJson(
                    new String(Base64.getDecoder().decode(parts[1]), DEFAULT_CHARSET), Map.class);
        } catch (UnsupportedEncodingException e) {
           throw new RuntimeException(e);
        }
    }
    public Map<String, String> getAuthHeader(String token){
        try{
            token = URLDecoder.decode(token, DEFAULT_CHARSET);
            String[] parts = token.split(PART_SPLIT);
            if(parts.length != AUTH_TOKEN_LENGTH){
                throw new RuntimeException("invalid length in token : " + token);
            }
            return Json.fromJson(
                    new String(Base64.getDecoder().decode(parts[0]), DEFAULT_CHARSET), Map.class);
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }
    public boolean validate(String token, long minutes) {
        boolean result = false;
        try {
            token = URLDecoder.decode(token, DEFAULT_CHARSET);
            String[] parts = token.split(PART_SPLIT);
            if(parts.length != AUTH_TOKEN_LENGTH){
                throw new RuntimeException("invalid length in token : " + token);
            }
            String self = parts[0] + PART_SPLIT + parts[1];
            String auth = parts[2];
            Map<String, String> headers = Json.fromJson(
                    new String(Base64.getDecoder().decode(parts[0]), DEFAULT_CHARSET), Map.class);
            String time = Objects.requireNonNull(headers).get(AuthConstraints.SSO_TOKEN_TIMESTAMP);
            SimpleDateFormat format = new SimpleDateFormat(AuthConstraints.SSO_TOKEN_TIME_PATTERN);
            Date date = format.parse(time);
            if(Calendar.getInstance().getTimeInMillis() - date.getTime() > minutes * ONE_MINUTES){
                throw new ExpireTimeOutException("the token is expired, value : " + token);
            }
            AuthTokenBean.Type type = AuthTokenBean.Type.of(headers.get(AuthTokenBean.ALG_NAME));
            String auth0 = getAuthString(type, tokenSecret + self, time);
            if(!auth.equals(auth0)){
                throw new RuntimeException("incorrect token : " + token);
            }
            result = true;
        }catch(ExpireTimeOutException e){
            throw e;
        }catch(Exception e){
            logger.info(e.getMessage());
        }
        return result;
    }

    private String getAuthString(AuthTokenBean.Type type, String source, String salt){
        String auth ;
        switch(type){
            default:
                auth = CryptoUtils.md5(source, salt, 2);
                break;
        }
        return auth;
    }
}
