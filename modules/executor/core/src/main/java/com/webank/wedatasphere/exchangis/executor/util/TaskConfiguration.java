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

import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Copy and simplify from 'Configuration' in 'DataX'
 * @author davidhua
 * 2019/12/20
 */
public class TaskConfiguration {
    public static final String SPLIT_CHAR = ".";
    private Object root;

    public static TaskConfiguration from(String json){
        json = PatternInjectUtils.inject(json, new String[]{});
        return new TaskConfiguration(json);
    }

    public static boolean searchKeyToInsertValue(TaskConfiguration configuration, String path,
                                                 String key, Object value){
        TaskConfiguration subConf = configuration.getConfiguration(path);
        Set<String> keys = subConf.getKeys();
        //search key
        for(String key0 : keys){
            if(key0.endsWith(SPLIT_CHAR + key)){
                configuration.set(StringUtils.join(new String[]{path, key0}, SPLIT_CHAR), value);
                return true;
            }
        }
        return false;
    }

    public static List<String> searchKeyPaths(TaskConfiguration configuration, String path,
                                         String key){
        List<String> result = new ArrayList<>();
        TaskConfiguration subConf = configuration.getConfiguration(path);
        Set<String> keys = subConf.getKeys();
        keys.forEach(key0 -> {
            if(key0.equals(key) || key0.endsWith(SPLIT_CHAR + key)){
                result.add(key0);
            }
        });
        return result;
    }
    /**
     * Serialize the configuration
     * @return
     */
    public String toJson(){
        return Json.toJson(root, null);
    }

    /**
     * Set value to path
     * @param path path
     * @param object object value
     * @return
     */
    public Object set(final String path, final Object object) {
        if(!checkPath(path)){
            return null;
        }
        Object result = this.get(path);
        setObject(path, extract(object));
        return result;
    }

    /**
     * Get value by path
     * @param path path
     * @return
     */
    public Object get(final String path){
        this.checkPath(path);
        return this.findObject(path);
    }

    /**
     * Get string value by path
     * if the result is null, use the default value
     * @param path path
     * @param defaultValue default value
     * @return
     */
    public String getString(final String path, String defaultValue){
        Object result = this.get(path);
        if(null == result){
            return defaultValue;
        }
        if(result instanceof String){
            return (String)result;
        }else if(result.getClass().isPrimitive() || isWrapClass(result.getClass())){
            return String.valueOf(result);
        }else{
            return Json.toJson(result, null);
        }
    }

    /**
     * get integer value by path
     * @param path path
     * @return
     */
    public Integer getInt(final String path){
        String result = this.getString(path);
        if(null == result){
            return null;
        }
        return Integer.valueOf(result);
    }

    /**
     * Get string value by path
     * @param path path
     * @return
     */
    public String getString(final String path){
        return getString(path, null);
    }

    /**
     * Get keys
     * @return
     */
    public Set<String> getKeys(){
        return getKeys(Integer.MAX_VALUE);
    }

    public Set<String> getKeys(int maxDepth){
        Set<String> collect = new HashSet<>();
        this.getKeysRecursive(this.root, "", collect, maxDepth);
        return collect;
    }

    public TaskConfiguration getConfiguration(final String path){
        Object object = this.get(path);
        if(null == object){
            return null;
        }
        return TaskConfiguration.from(Json.toJson(object, null));
    }

    private Object findObject(final String path){
        if(StringUtils.isBlank(path)){
            return this.root;
        }
        Object target = this.root;
        for(final String each : split2List(path)){
            if(isPathMap(each) && target instanceof Map){
                target = ((Map<String, Object>)target).get(each);
            }else if (isPathList(each) && target instanceof List){
                String index = each.replace("[", "").replace("]", "");
                if(!StringUtils.isNumeric(index)){
                    throw new IllegalArgumentException("index value must be numeric, value: " + index);
                }
                target = ((List<Object>)target).get(Integer.valueOf(index));
            }else{
                target = null;
                break;
            }
        }
        return target;
    }

    private void setObject(final String path, final Object object){
        Object newRoot = setObjectRecursive(this.root, split2List(path), 0, object);
        boolean isSuit = null != newRoot && (newRoot instanceof List || object instanceof Map);
        if(isSuit){
            this.root = newRoot;
        }
    }

    private Object setObjectRecursive(Object current, final List<String> paths,
                                      int index, final Object value){
        if(index >= paths.size()){
            return value;
        }
        String path = paths.get(index).trim();
        if(isPathMap(path)){
            //current object is not map
            Map<String, Object> mapping;
            if(!(current instanceof Map)){
                mapping = new HashMap<>(1);
                mapping.put(path, buildObject(paths.subList(index + 1, paths.size()), value));
                return mapping;
            }
            mapping = (Map<String, Object>)current;
            //current map does not have key
            if(!mapping.containsKey(path)){
                mapping.put(path, buildObject(paths.subList(index + 1, paths.size()), value));
                return mapping;
            }
            mapping.put(path, setObjectRecursive(mapping.get(path),
                    paths, index + 1, value));
            return mapping;
        }
        if(isPathList(path)){
            List<Object> lists;
            int listIndex = getIndex(path);
            //current object is not list
            if(!(current instanceof List)){
               lists = expand(new ArrayList<>(listIndex + 1), listIndex + 1);
               lists.set(listIndex, buildObject(paths.subList(index + 1, paths.size()), value));
               return lists;
            }
            lists = (List<Object>) current;
            lists = expand(lists, listIndex + 1);
            //current list does not have the index
            if(null == lists.get(listIndex)){
                lists.set(listIndex, buildObject(paths.subList(index + 1, paths.size()), value));
                return lists;
            }
            lists.set(listIndex, setObjectRecursive(lists.get(listIndex),
                    paths, index + 1, value));
            return lists;
        }
        throw new RuntimeException("system error");
    }

    private Object buildObject(final List<String> paths, final Object object){
        if(null == paths ){
            throw new IllegalArgumentException("paths cannot be null");
        }
        if(1 == paths.size() && StringUtils.isBlank(paths.get(0))){
            return object;
        }
        Object child = object;
        for(int i = paths.size() - 1; i >= 0; i--){
            String path = paths.get(i);
            if(isPathMap(path)){
                Map<String, Object> mapping = new HashMap<>(1);
                mapping.put(path, child);
                child = mapping;
                continue;
            }
            if(isPathList(path)){
                int index = getIndex(path);
                List<Object> lists = new ArrayList<>(index + 1);
                expand(lists, index + 1);
                lists.set(index, child);
                child = lists;
                continue;
            }
            throw new IllegalArgumentException("illegal path");
        }
        return child;
    }

    private Object extract(final Object object){
        if(object instanceof TaskConfiguration){
            return ((TaskConfiguration)object).root;
        }
        if(object instanceof List){
            List<Object> result = new ArrayList<>();
            for(final Object each : (List)object){
                result.add(extract(each));
            }
            return result;
        }
        if(object instanceof Map){
            Map map = (Map)object;
            Map<String, Object> result = new HashMap<>(map.size());
            for(final Object key : map.keySet()){
                result.put(String.valueOf(key), extract(map.get(key)));
            }
            return result;
        }
        return object;
    }

    private boolean isPathList(final String path){
        return path.contains("[") && path.contains("]");
    }

    private boolean isPathMap(final String path){
        return StringUtils.isNotBlank(path) && !isPathList(path);
    }

    private List<String> split2List(final String path){
        return Arrays.asList(StringUtils.split(path.replace("[", ".["), "."));
    }

    private int getIndex(final String index) {
        return Integer.valueOf(index.replace("[", "").replace("]", ""));
    }

    private boolean checkPath(final String path){
        if(null == path){
            return false;
        }
        for(String each : StringUtils.split(SPLIT_CHAR)){
            if(StringUtils.containsWhitespace(each)){
                throw new IllegalArgumentException("cannot contains white space in : " + path);
            }
        }
        return true;
    }

    private List<Object> expand(List<Object> list, int size) {
        int expand = size - list.size();
        while (expand-- > 0) {
            list.add(null);
        }
        return list;
    }

    private void getKeysRecursive(final Object current, String path, Set<String> collect,int depth){
        if(depth-- <= 0){
            collect.add(path);
            return;
        }
        if(current instanceof Map){
            Map mapping = (Map)current;
            for(final Object key : mapping.keySet()){
                String keyStr = String.valueOf(key).trim();
                if(StringUtils.isBlank(path)){
                    getKeysRecursive(mapping.get(key), keyStr, collect, depth);
                }else{
                    getKeysRecursive(mapping.get(key), path + SPLIT_CHAR + keyStr, collect, depth);
                }
            }
        }else if(current instanceof List){
            List lists = (List) current;
            for(int i = 0; i < lists.size(); i++){
                getKeysRecursive(lists.get(i), path + String.format("[%d]", i), collect, depth);
            }
        }else{
            collect.add(path);
        }
    }

    private static boolean isWrapClass(Class clz){
        try{
            return ((Class)clz.getField("TYPE").get(null)).isPrimitive();
        }catch(Exception e){
            return false;
        }
    }
    private TaskConfiguration(final String json){
        this.root = Json.fromJson(json, Map.class);
    }

}
