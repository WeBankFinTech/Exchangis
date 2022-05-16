package com.webank.wedatasphere.exchangis.project.server.mapper;

import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProjectUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/5/11 11:17
 */
public interface ProjectUserMapper {

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
}
