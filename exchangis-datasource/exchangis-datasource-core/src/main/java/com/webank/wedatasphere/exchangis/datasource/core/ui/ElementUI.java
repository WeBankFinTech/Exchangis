package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.Map;

public interface ElementUI<T> {
    /**
     * Type enum
     */
    enum Type {
        NONE, TEXTAREA, INPUT, OPTION, MAP
    }

    /**
     * Field name
     * @return string
     */
    String getField();

    /**
     * Label
     * @return label string
     */
    String getLabel();

    /**
     *  Type name
     * @return string
     */
    String getType();

    Integer getSort();

    /**
     * Value store
     * @return
     */
    T getValue();

    /**
     * Default value
     * @return
     */
    T getDefaultValue();

    /**
     * Get value from params
     * @param params
     * @return
     */
    void setValue(Map<String, Object> params);
}
