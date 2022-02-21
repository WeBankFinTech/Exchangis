package com.webank.wedatasphere.exchangis.job.utils;

import com.webank.wedatasphere.exchangis.job.constraints.LabelSerializeConstraints;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.manager.label.entity.SerializableLabel;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Label converter
 */
public class LabelConvertUtils {

    /**
     * Convert labels to string
     * @param labels Map of label entities
     */
    public static String labelsToString(Map<String, Object> labels){
        return labels.entrySet().stream().map( entry -> {
            String labelKey = entry.getKey();
            Object entryValue = entry.getValue();
            return labelKey + LabelSerializeConstraints.LABEL_KV_COMBINE_SYMBOL +
                    (entryValue instanceof SerializableLabel? ((SerializableLabel<?>)entryValue).getStringValue() : entryValue.toString());
        }).collect(Collectors.joining(LabelSerializeConstraints.LABEL_ENTITY_SPLITTER_SYMBOL));
    }

    /**
     * Convert string to label map
     * @param labelStr label string
     * @return label map
     */
    public static Map<String, Object> stringToLabelMap(String labelStr){
        if (StringUtils.isNotBlank(labelStr)) {
            Map<String, Object> candidateLabels = new HashMap<>();
            String[] labelEntities = labelStr.split(LabelSerializeConstraints.LABEL_ENTITY_SPLITTER_SYMBOL);
            for (String labelEntity : labelEntities) {
                String[] kvContent = labelEntity.split(LabelSerializeConstraints.LABEL_KV_COMBINE_SYMBOL);
                String key = kvContent[0];
                String value = kvContent.length > 1 ? kvContent[1] : null;
                candidateLabels.put(key, value);
            }
            return candidateLabels;
        }
        return null;
    }

}
