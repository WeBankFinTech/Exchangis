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

import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author davidhua
 * 2018/11/3
 */
@Component
@ConfigurationProperties(prefix = "auth.token")
public class WhiteList {
    private List<Pair> whiteList;
    public List<String> getList(String id, String pwd, String address){
        pwd = CryptoUtils.md5(pwd, id, 2);
        for(Pair pair : whiteList){
            if (pair.id.equals(id) && pair.pwd.equals(pwd)) {
                if(!pair.address.isEmpty()){
                    return pair.address.contains(address)? pair.allowedUris : Collections.emptyList();
                }
                return pair.allowedUris;
            }
        }
        return Collections.emptyList();
    }

    public List<Pair> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<Pair> whiteList) {
        this.whiteList = whiteList;
    }

    public static class Pair{
        private String id;
        private String pwd;
        private List<String> address = new ArrayList<>();
        private List<String> allowedUris = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public List<String> getAddress() {
            return address;
        }

        public void setAddress(List<String> address) {
            this.address = address;
        }

        public List<String> getAllowedUris() {
            return allowedUris;
        }

        public void setAllowedUris(List<String> allowedUris) {
            this.allowedUris = allowedUris;
        }
    }

}
