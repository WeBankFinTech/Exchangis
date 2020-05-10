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


import java.util.HashMap;
import java.util.Map;

/**
 * Build jwt token from ticket
 * @author davidhua
 * 2018/10/16
 */
public class AuthTokenBean {
    /**
     * simply use MD5 to validate
     */
    public static final String ALG_NAME = "alg";
    enum Type{
        //md5 algorithm
        MD5;
        static Map<String, Type> types = new HashMap<>();
        static{
            types.put("md5", MD5);
        }
        static Type of(String name){
            Type result = types.get(name.toLowerCase());
            if(null == result){
                throw new IllegalArgumentException("type name: "+ name);
            }
            return result;
        }
    }
    /**
     * headers
     */
    private Map<String, String> headers = new HashMap<>();
    /**
     * core domain
     */
    private Map<String, String> claims = new HashMap<>();

    public AuthTokenBean(){
        headers.put(ALG_NAME, "MD5");
    }

    public Map<String, String> getHeaders(){
        return headers;
    }

    public Map<String, String> getClaims(){
        return claims;
    }

    public void setHeaders(Map<String, String> map){
        this.headers = map;
    }

    public void setClaims(Map<String, String> map){
        this.claims = map;
    }
}
