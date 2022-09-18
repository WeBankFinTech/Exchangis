package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformDefine;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.FieldMappingRule;

import java.util.List;

/**
 * Transform dao in rendering job
 */
public interface JobTransformRuleDao {

    /**
     * Get transform rule definition list
     * @param dataSourceType data source type
     * @return rule list
     */
    List<TransformDefine> getTransformDefines(String dataSourceType);

    /**
     * Get mapping rules
     * @param dataSourceType data source type
     * @return rule list
     */
    List<FieldMappingRule> getMappingRules(String dataSourceType);
}
