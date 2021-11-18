package com.webank.wedatasphere.exchangis.appconn.utils;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.linkis.manager.label.entity.SerializableLabel;

import java.util.List;
import java.util.stream.Collectors;

public class AppconnUtils {
    public static String changeDssLabelName(List<DSSLabel> list){
        String dssLabelStr="";
        if(list != null && !list.isEmpty()){
            dssLabelStr=list.stream().map(SerializableLabel::getStringValue).collect(Collectors.joining(","));
        }
        return dssLabelStr;
    }
}
