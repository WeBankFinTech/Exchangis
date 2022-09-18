package com.webank.wedatasphere.exchangis.job.server.render.transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform definition
 */
public class TransformDefine extends TransformRule{

    static{
        TransformRule.typeClasses.put(Types.DEF.name(), TransformDefine.class);
    }

    private List<String> types = new ArrayList<>();
    public TransformDefine(Types type, String ruleSource) {
        super(type, ruleSource);

    }
}
