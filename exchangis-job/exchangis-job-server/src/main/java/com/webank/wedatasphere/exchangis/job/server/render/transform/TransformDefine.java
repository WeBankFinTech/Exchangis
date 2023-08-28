package com.webank.wedatasphere.exchangis.job.server.render.transform;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.FieldMappingRuleEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Transform definition
 */
public class TransformDefine extends TransformRule{

    static{
        TransformRule.typeClasses.put(TransformRule.Types.DEF.name(), TransformDefine.class);
    }

    private List<String> types = new ArrayList<>();

    public TransformDefine(){

    }
    public TransformDefine(TransformRule.Types type, String ruleSource) {
        super(type, ruleSource);
        if (StringUtils.isNotBlank(ruleSource)){
            TransformDefine definition = Json.fromJson(ruleSource, TransformDefine.class);
            Optional.ofNullable(definition).ifPresent(def -> {
                this.types = def.types;
            });
        }
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
