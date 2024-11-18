package com.webank.wedatasphere.exchangis.datasource.core.splitter;

import com.webank.wedatasphere.exchangis.common.util.json.JsonEntity;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Split strategy: field split
 */
public class DataSourceFieldSplitStrategy implements DataSourceSplitStrategy{
    public static final String NAME = "field-split-strategy";
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public List<Map<String, Object>> getSplitValues(Map<String, Object> dataSourceParams,
                                                    DataSourceSplitKey splitKey) {
        if (Objects.nonNull(splitKey) && Objects.nonNull(dataSourceParams)){
            List<Map<String, Object>> result = new ArrayList<>();
            doSplit(dataSourceParams, splitKey, (parent, prefix, splitPart) -> {
//                splitPart.forEach((partKey, partValue) -> parent.set(prefix + partKey, partValue));
                result.add(splitPart);
            });
            return result;
        }
        return Collections.singletonList(dataSourceParams);
    }

    @SuppressWarnings("unchecked")
    private void doSplit(Map<String, Object> dataSourceParams,
                         DataSourceSplitKey splitKey, SplitPartConsumer splitPartConsumer){
        JsonEntity configuration = JsonEntity.from(dataSourceParams);
        List<String> keyPaths = JsonEntity.searchKeyPaths(configuration, "", splitKey.getSplitKey(),
                StringUtils.splitPreserveAllTokens(splitKey.getSplitKey(), JsonEntity.SPLIT_CHAR).length);
        if (!keyPaths.isEmpty()){
            String keyPath = keyPaths.get(0);
            String prefix = keyPath.substring(0, Math.max(keyPath.lastIndexOf(JsonEntity.SPLIT_CHAR), -1) + 1);
            Object splitValues = configuration.get(keyPath);
            if (List.class.isAssignableFrom(splitValues.getClass())){
                // Empty the split node
                configuration.set(splitKey.getSplitKey(), null);
                ((List<?>) splitValues).forEach(splitValue -> {
                    Map<String, Object> splitPart = null;
                    try {
                        splitPart = (Map<String, Object>)splitValue;
                    } catch (Exception e){
                        // Ignore the class cast exception
                    }
                    if (Objects.nonNull(splitPart)) {
                        splitPartConsumer.accept(configuration.getConfiguration(""), prefix, splitPart);
                    }
                });

            }
        }
    }

    @FunctionalInterface
    private interface SplitPartConsumer{
        /**
         * Accept
         * @param parent parent node
         * @param prefix prefix
         * @param splitPart split part
         */
        void accept(JsonEntity parent, String prefix, Map<String, Object> splitPart);
    }
    public static void main(String[] args) {
        String json = "{\"conn_ins\":[{\"hello\":\"ok\", \"world\":\"right\"},{\"hello1\":\"ok\", \"world\":\"right\"}]}";
        Map<String, Object> map = Json.fromJson(json, Map.class);
        JsonEntity entity = JsonEntity.from(map);
        List<String> paths = JsonEntity.searchKeyPaths(entity, "", "world");
        for(String path : paths){
            entity.set(path, "********");
        }
        System.out.println(entity.toJson());
//        DataSourceSplitStrategy splitStrategy = new DataSourceFieldSplitStrategy();
//        splitStrategy
//                .getSplitValues(map,
//                        new DataSourceSplitKey("conn_ins", new String[]{})).forEach(v -> {
//                            System.out.println(Json.toJson(v, null));
//                });
//        System.out.println(Json.toJson(map, null));
    }
}
