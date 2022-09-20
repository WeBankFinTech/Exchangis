package com.webank.wedatasphere.exchangis.job.utils;

import com.google.gson.Gson;
import com.webank.wedatasphere.exchangis.job.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.job.auditlog.TargetTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author tikazhang
 * @Date 2022/9/19 20:07
 */
public class AuditLogUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogUtils.class);

    /**
     * 打印审计日志，id类的属性都是String
     * @param user 执行操作的用户名
     * @param targetType 操作针对的对象类型
     * @param targetId 操作针对的对象id
     * @param targetName 操作针对的对象名称
     * @param operateType 操作类型
     * @param params 操作相关的参数
     */
    public static void printLog(String user, TargetTypeEnum targetType,
                                String targetId, String targetName, OperateTypeEnum operateType, Object params) {
        //String detailInfo=new Gson().toJson(params);
        LOGGER.info("AuditLog print test");
        String detailInfo=params.toString();
        LOGGER.info("[{}],[{}],[{}],[{}],[{}],[{}],[{}]",
                new Date(),user, targetType.getName(),
                targetId,targetName,operateType.getName(), detailInfo);
    }

}
