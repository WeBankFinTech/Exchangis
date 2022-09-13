package com.webank.wedatasphere.exchangis.project.server.utils;

import com.webank.wedatasphere.exchangis.project.server.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectExceptionCode;
import com.webank.wedatasphere.exchangis.project.server.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tikazhang
 * @Date 2022/5/10 20:10
 */
public class ExchangisAuthorityUtils {

    @Autowired
    private static ProjectMapper projectMapper;

    /**
     * @param username username
     * @param project project
     * @param operationType enum("view","edit","exec","delete")
     * @return
     */
    public static boolean hasAuthority(String username, ExchangisProjectInfo project, OperationType operationType) throws ExchangisProjectErrorException {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(username) &&
                Objects.nonNull(project) &&
                Objects.nonNull(operationType)) {
            switch (operationType) {
                case VIEW:
                    List<String> viewUsers = Arrays.stream(project.getViewUsers().split(",")).distinct().collect(Collectors.toList());
                    return viewUsers.contains(username);
                case EDIT:
                    List<String> editUsers = Arrays.stream(project.getEditUsers().split(",")).distinct().collect(Collectors.toList());
                    return editUsers.contains(username);
                case EXEC:
                    List<String> execUsers = Arrays.stream(project.getExecUsers().split(",")).distinct().collect(Collectors.toList());
                    return execUsers.contains(username);
                case DELETE:
                    return org.apache.commons.lang3.StringUtils.equals(username, project.getCreateUser());
                default:
                    throw new ExchangisProjectErrorException(ExchangisProjectExceptionCode.UNSUPPORTED_OPERATION.getCode(), "Unsupported operationType");
            }
        }
        return false;
    }
}
