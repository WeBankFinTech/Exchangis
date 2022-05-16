package com.webank.wedatasphere.exchangis.job.server.utils;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copy and simplify from 'Configuration' in 'DataX'
 */
public class JsonEntity {
    public static final String SPLIT_CHAR = ".";
    private Object root;

    public static JsonEntity from(String json){
        return new JsonEntity(json);
    }

    public static JsonEntity from(Map<String, Object> map){
        return new JsonEntity(map);
    }


    public static boolean searchKeyToInsertValue(JsonEntity configuration, String path,
                                                 String key, Object value){
        JsonEntity subConf = configuration.getConfiguration(path);
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


    public static List<String> searchKeyPaths(JsonEntity configuration, String path,
                                              String key){
        return searchKeyPaths(configuration, path, key, Integer.MAX_VALUE);
    }

    public static List<String> searchKeyPaths(JsonEntity configuration, String path,
                                              String key, int depth){
        List<String> result = new ArrayList<>();
        JsonEntity subConf = configuration.getConfiguration(path);
        Set<String> keys = subConf.getKeys(depth);
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

    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap(){
        if (this.root instanceof Map){
            return new HashMap<>((Map<String, Object>) root);
        }
        return null;
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
     * get double value by path
     * @param path path
     * @return
     */
    public Double getDouble(final String path){
        String result = this.getString(path);
        if(null == result){
            return null;
        }
        return Double.valueOf(result);
    }

    /**
     * get long value by path
     * @param path path
     * @return
     */
    public Long getLong(final String path){
        String result = this.getString(path);
        if(null == result){
            return null;
        }
        return Long.valueOf(result);
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

    public JsonEntity getConfiguration(final String path){
        Object object = this.get(path);
        if(null == object){
            return null;
        }
        return from(Json.toJson(object, null));
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
        if(object instanceof JsonEntity){
            return ((JsonEntity)object).root;
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
        return Arrays.asList(StringUtils.split(path.replace("[", ".["), ".")).stream()
                .map(value -> value.replace("0x2e", ".")).collect(Collectors.toList());
    }

    private int getIndex(final String index) {
        return Integer.parseInt(index.replace("[", "").replace("]", ""));
    }

    private boolean checkPath(final String path){
        if(null == path){
            return false;
        }
        for(String each : StringUtils.split(SPLIT_CHAR)){
            if(StringUtils.isWhitespace(each)){
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

    public static String encodePath(String path){
        if (StringUtils.isNotBlank(path)){
            return path.replace(".", "0x2e");
        }
        return path;
    }
    private JsonEntity(final String json){
        this.root = Json.fromJson(json, Map.class);
    }

    private JsonEntity(final Map<String, Object> jsonMap){
        this.root = new HashMap<>(jsonMap);
    }
}
