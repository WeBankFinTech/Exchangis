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

package com.webank.wedatasphere.exchangis.common.mybatis.plugin;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Map handler
 * @author davidhua
 * 2019/5/22
 */
@Intercepts({@Signature(type=ResultSetHandler.class, method="handleResultSets", args={Statement.class})})
public class MapInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MapInterceptor.class);
    private static final String MAP_TYPE = "java.util.Map";
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private static Map<String, MapRule> mapRuleHashMap = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ResultSetHandler handler = (ResultSetHandler)invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(handler,
                DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
        MappedStatement statement = (MappedStatement)metaObject.getValue("mappedStatement");
        List<ResultMap> resultMaps = statement.getResultMaps();
        if(resultMaps.size() > 0){
            String type = resultMaps.get(0).getType().getName();
            if(null != type && type.equals(MAP_TYPE)){
                logger.trace(MAP_TYPE + " resultMap");
                String id = statement.getId();
                MapRule mapRule = mapRuleHashMap.get(id);
                if(null == mapRule){
                    String className = id.substring(0, id.lastIndexOf("."));
                    Class<?> clz = Class.forName(className);
                    String method = id.substring(id.lastIndexOf(".") + 1);
                    Method[] methods = clz.getMethods();
                    for(Method m : methods){
                        if(m.getName().equalsIgnoreCase(method)){
                            mapRule = m.getAnnotation(MapRule.class);
                            mapRuleHashMap.put(className, mapRule);
                            break;
                        }
                    }
                }
                if(null != mapRule){
                    return map((Statement)invocation.getArgs()[0], mapRule);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if(target instanceof  ResultSetHandler){
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Object map(Statement statement, MapRule mapRule) throws SQLException {
        logger.trace("Start to build map result");
        Map<Object, Object> buildMap = new HashMap<>(8);
        List<Object> result = new ArrayList<>();
        try (ResultSet resultSet = statement.getResultSet()) {
            if (null != resultSet) {
                while (resultSet.next()) {
                    buildMap.put(resultSet.getObject(mapRule.key()),
                            resultSet.getObject(mapRule.value()));
                }
            }
        }
        logger.trace("Build map result: " + Json.toJson(buildMap, null));
        result.add(buildMap);
        return result;
    }
}
