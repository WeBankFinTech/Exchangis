package com.webank.wedatasphere.exchangis.project.server.utils;

import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectExceptionCode;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tikazhang
 * @Date 2022/5/10 20:10
 */
public class ProjectAuthorityUtils {

    /**
     * @param username      username
     * @param project       project
     * @param operationType enum("PROJECT_QUERY","PROJECT_ALTER")
     * @return
     */
    public static boolean hasProjectAuthority(String username, ExchangisProjectInfo project, OperationType operationType) throws ExchangisProjectErrorException {
        if (StringUtils.isNotEmpty(username) &&
                Objects.nonNull(project) &&
                Objects.nonNull(operationType)) {
            // Create users have all rights to the project.
            List<String> viewUsers = Arrays.stream(project.getViewUsers().split(",")).distinct().collect(Collectors.toList());
            List<String> editUsers = Arrays.stream(project.getEditUsers().split(",")).distinct().collect(Collectors.toList());
            List<String> execUsers = Arrays.stream(project.getExecUsers().split(",")).distinct().collect(Collectors.toList());

            switch (operationType) {
                case PROJECT_QUERY:
                    return StringUtils.equals(username, project.getCreateUser()) ||
                            viewUsers.contains(username) ||
                            editUsers.contains(username) ||
                            execUsers.contains(username);
                case PROJECT_ALTER:
                    return StringUtils.equals(username, project.getCreateUser());
                default:
                    throw new ExchangisProjectErrorException(ExchangisProjectExceptionCode.UNSUPPORTED_OPERATION.getCode(), "Unsupported operationType");
            }
        }
        return false;
    }
}
