package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Transform dao in rendering job
 */
public interface JobTransformRuleDao {

    /**
     * Get transform rule list
     * @param ruleType rule type
     * @param dataSourceType data source type
     * @return rule list
     */
    List<TransformRule> getTransformRules(@Param("ruleType") String ruleType,
                                          @Param("dataSourceType") String dataSourceType);

}
