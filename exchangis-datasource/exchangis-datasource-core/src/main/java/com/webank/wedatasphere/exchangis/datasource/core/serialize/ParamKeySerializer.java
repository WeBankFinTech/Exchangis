package com.webank.wedatasphere.exchangis.datasource.core.serialize;

import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;

/**
 * Serialize the parameter key if in need
 */
public interface ParamKeySerializer {

    void serialize(Object value, DataSourceParamKeyDefinition.ValueType valueType,
                   DataSourceParamKeyDefinition.ValueType... nestValueType);
}
