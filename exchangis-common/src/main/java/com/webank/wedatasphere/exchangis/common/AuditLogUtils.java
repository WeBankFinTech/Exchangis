package com.webank.wedatasphere.exchangis.common;


import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author tikazhang
 * @Date 2022/9/19 20:07
 */
public class AuditLogUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogUtils.class);

    public static void printLog(HttpServletRequest request, TargetTypeEnum targetType,
                                String targetId, String targetName, OperateTypeEnum operateType, Object params) {
        String detailInfo=params.toString();
        String loginUser = UserUtils.getLoginUser(request);
        String originUser = SecurityFilter.getLoginUsername(request);
        LOGGER.info("[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}]",
                new Date(), loginUser, "proxyUser is: " + originUser, "Exchangis-1.1.6", targetType.getName(),
                targetId, targetName, operateType.getName(), detailInfo);
    }

    /**
     * 打印审计日志，id类的属性都是String
     * @param user 执行操作的用户名
     * @param targetType 操作针对的对象类型
     * @param targetId 操作针对的对象id
     * @param targetName 操作针对的对象名称
     * @param operateType 操作类型
     * @param params 操作相关的参数
     */
    public static void printLog(String user, String proxyUser, TargetTypeEnum targetType,
                                String targetId, String targetName, OperateTypeEnum operateType, Object params) {
        String detailInfo=params.toString();
        LOGGER.info("[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}]",
                new Date(),user, "proxyUser is: " + proxyUser, "Exchangis-1.1.6", targetType.getName(),
                targetId, targetName,operateType.getName(), detailInfo);
    }
}
