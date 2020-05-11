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

package com.webank.wedatasphere.exchangis.datasource.conns.cache;

import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.google.common.cache.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author davidhua
 * 2019/1/9
 */
@Component
public class ConnCacheManager implements CacheManager {
    private ConcurrentHashMap<String, Cache> cacheStore = new ConcurrentHashMap<>();
    @Resource
    private Configuration conf;
    @Override
    public <V> Cache<String, V> buildCache(String cacheId, RemovalListener<String, V> removalListener) {
        Cache<String, V> vCache = CacheBuilder.newBuilder()
                .maximumSize(conf.getConnCacheSize())
                .expireAfterWrite(conf.getConnCacheTime(), TimeUnit.SECONDS)
                .removalListener(removalListener)
                .build();
        cacheStore.putIfAbsent(cacheId, vCache);
        return vCache;
    }

    @Override
    public <V> LoadingCache<String, V> buildCache(String cacheId, CacheLoader<String, V> loader,
                                                  RemovalListener<String, V> removalListener) {
        LoadingCache<String, V> vCache = CacheBuilder.newBuilder()
                .maximumSize(conf.getConnCacheSize())
                .expireAfterWrite(conf.getConnCacheTime(), TimeUnit.SECONDS)
                .removalListener(removalListener)
                .build(loader);
        cacheStore.putIfAbsent(cacheId, vCache);
        return vCache;
    }
}
