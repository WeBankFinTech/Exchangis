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

package com.webank.wedatasphere.exchangis.gateway.auth.cas;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import java.util.concurrent.TimeUnit;

/**
 * @author davidhua
 * 2018/10/18
 */
@Component
public class CasSessionStorage {
    private static final Logger LOG = LoggerFactory.getLogger(CasSessionStorage.class);
    private static final Integer GB_PER = 1000000;
    private Cache<String, WebSession> cache = CacheBuilder.newBuilder()
            .maximumWeight(GB_PER).expireAfterAccess(1, TimeUnit.DAYS).weigher((Weigher<String, WebSession>) (key, value) -> 5).build();
    public void addSession(String ticket, WebSession session){
        if(!session.isExpired()){
            if(null == cache.getIfPresent(ticket)) {
                if(LOG.isInfoEnabled()){
                    LOG.info("Store CAS session, CAS ticket: "+ticket+", sessionId: "+session.getId());
                }
                cache.put(ticket, session);
            }
        }
    }

    public boolean exist(String ticket){
        return null != cache.getIfPresent(ticket);
    }
    public WebSession removeSession(String ticket){
        WebSession session = cache.getIfPresent(ticket);
        if(null != session){
            if(LOG.isInfoEnabled()){
                LOG.info("Remove CAS session, CAS ticket: " + ticket);
            }
            cache.invalidate(ticket);
        }
        return session;
    }
}
