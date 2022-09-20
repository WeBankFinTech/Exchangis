package com.webank.wedatasphere.exchangis.job.server.render.transform.def;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformDefine;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRulesFusion;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default transform define rules fusion
 */
public class DefaultTransformDefineRulesFusion implements TransformRulesFusion<TransformDefine> {
    @Override
    public TransformDefine fuse(TransformDefine sourceRule, TransformDefine sinkRule) {
        Set<String> typeSet = new HashSet<>();
        typeSet.addAll(sourceRule.getTypes());
        typeSet.addAll(sinkRule.getTypes());
        // Filter the unrecognized value
        typeSet = typeSet.stream().filter( type -> {
            try {
                TransformTypes.valueOf(type);
                return true;
            }catch (Exception e){
                //Ignore
                return false;
            }
        }).collect(Collectors.toSet());
        if (typeSet.contains(TransformTypes.NONE.name())){
            typeSet.clear();
        }
        TransformDefine fusedDefine = new TransformDefine(TransformRule.Types.DEF, null);
        fusedDefine.setTypes(new ArrayList<>(typeSet));
        return fusedDefine;
    }
}
