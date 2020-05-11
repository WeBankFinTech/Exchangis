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

package com.webank.wedatasphere.exchangis.datasource;

import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author davidhua
 * 2018/9/20
 */
@Component
@PropertySource("classpath:datasource-cfg.properties")
public class Configuration {

    @Value("${store.prefix}")
    private String storePrefix;
    @Value("${store.tmp}")
    private String storeTmp;
    @Value("${store.uri}")
    private String storeUri;

    @Value("${kerberos.krb5.path:krb5.conf}")
    private String kbKrb5Path;

    @Value("${kerberos.principle.hiveMetaStore:hadoop/_HOST@EXAMPLE.COM}")
    private String kbPrincipleHive;

    @Value("${ldap.switch:false}")
    private boolean ldapSwitch;

    @Value("${ldap.url:ldap}")
    private String ldapUrl;

    @Value("${ldap.baseDN:baseDN}")
    private String ldapBaseDn;

    @Value("${data_source.connect.cache.time:600}")
    private Integer connCacheTime;
    @Value("${data_source.connect.cache.size:100}")
    private Integer connCacheSize;

    private enum AuthFileType{
        /*kerberos file*/
        KB,
        /*keyfile*/
        KEY;
        static final Map<String, String> TYPE_URI = new HashMap<>();
        static{
            TYPE_URI.put(AuthType.KERBERS, StringUtils.lowerCase(KB.name()));
            TYPE_URI.put(AuthType.KEYFILE, StringUtils.lowerCase(KEY.name()));
        }
    }
    public String getStoreTmp() {
        return storeTmp;
    }

    public String getStoreUri(){
        return storeUri;
    }

    public String getStoreUrl(String authType){
        return storeUri + IOUtils.DIR_SEPARATOR_UNIX +
                AuthFileType.TYPE_URI.getOrDefault(authType, "default") + IOUtils.DIR_SEPARATOR_UNIX;
    }

    public String getStorePersist(String authType) {
        return storePrefix + IOUtils.DIR_SEPARATOR_UNIX +
                AuthFileType.TYPE_URI.getOrDefault(authType, "default") + IOUtils.DIR_SEPARATOR_UNIX;
    }

    public String getKbKrb5Path() {
        return kbKrb5Path;
    }

    public Integer getConnCacheTime() {
        return connCacheTime;
    }

    public Integer getConnCacheSize() {
        return connCacheSize;
    }

    public String getStorePrefix() {
        return storePrefix;
    }

    public String getKbPrincipleHive() {
        return kbPrincipleHive;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public String getLdapBaseDn() {
        return ldapBaseDn;
    }

    public boolean isLdapSwitch() {
        return ldapSwitch;
    }
}
