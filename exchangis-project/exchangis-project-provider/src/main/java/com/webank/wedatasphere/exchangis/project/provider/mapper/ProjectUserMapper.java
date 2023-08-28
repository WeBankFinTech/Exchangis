package com.webank.wedatasphere.exchangis.project.provider.mapper;

import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/5/11 11:17
 */
public interface ProjectUserMapper {

    /**
     * query projectUser
     * @param projectUser
     */
    ExchangisProjectUser queryProjectUser(ExchangisProjectUser projectUser);

    /**
     * add projectUsers
     * @param projectUsers
     */
    void addProjectUser(@Param("projectUsers") List<ExchangisProjectUser> projectUsers);

    /**
     * update projectUsers
     * @param projectUsers
     */
    void updateProjectUser(@Param("projectUsers") List<ExchangisProjectUser> projectUsers);

    /**
     * delete projectUsers
     * @param projectId
     */
    void deleteProjectUser(Long projectId);
}
