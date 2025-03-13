package com.webank.wedatasphere.exchangis.project.provider.mapper;

import com.webank.wedatasphere.exchangis.project.entity.domain.ProjectPageQuery;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Project mapper
 */
public interface ProjectMapper {

    /**
     * Query the page list
     * @param pageQuery page query
     * @return page list
     */
    List<ExchangisProject> queryPageList(ProjectPageQuery pageQuery);

    List<ExchangisProject> queryPageInAll(ProjectPageQuery pageQuery);
    /**
     * Get detail by id
     * @param projectId project id
     * @return project entity
     */
    ExchangisProject getDetailById(Long projectId);

    /**
     * Get basic info by id
     * @param projectId project id
     * @return project entity
     */
    ExchangisProject getBasicById(Long projectId);
    /**
     * Insert project
     * @param project project entity
     * @return project id
     */
    long insertOne(ExchangisProject project);

    /**
     * If exists
     * @param projectId project id
     * @param projectName project name
     * @return int
     */
    Integer existsOne(@Param("projectId")Long projectId, @Param("projectName")String projectName);

    /**
     * Update one
     * @param project project entity
     */
    void updateOne(ExchangisProject project);

    /**
     * Update one
     * @param project project entity
     */
    void batchUpdate(@Param("list") List<ExchangisProject> project);

    /**
     * Delete project
     * @param projectId
     */
    void deleteOne(Long projectId);

    /**
     * Delete project by name
     * @param name
     */
    void deleteByName(String name);

    /**
     * get projects by name
     * @param projectName
     * @return List
     */
    List<ExchangisProject> getDetailByName(@Param("projectName") String projectName);

    ExchangisProject selectByName(String name);

    List<Long> selectByDomain(String domain);

    /**
     * get projects authoritis
     * @param projectId
     * @param loginUser
     * @return List
     */
    List<String> getAuthorities(@Param("projectId") Long projectId, @Param("loginUser") String loginUser);

    List<ExchangisProject> queryByUser(ProjectQueryVo projectQueryVo);

    List<ExchangisProject> queryByUserProjects(@Param("userName") String userName, @Param("projectIds") List<Long> projectIds);

    /**
     * Recycle user project
     * @param userName
     * @param handover
     */
    void recycleUserProject(@Param("userName") String userName, @Param("handover") String handover,
                            @Param("projectIds") List<Long> projectIds);
}
