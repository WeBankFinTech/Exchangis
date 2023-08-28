package com.webank.wedatasphere.exchangis.project.server.service;


import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectUserVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;

/**
 * Project query
 */
public interface ProjectService {

     /**
      * Create project
      * @param projectVo project vo
      * @return new project id
      */
     long createProject(ExchangisProjectInfo projectVo, String userName);

     /**
      * Exists project
      * @param projectId project id
      * @param projectName project name
      * @return boolean
      */
     boolean existsProject(Long projectId, String projectName);

     /**
      * Update project
      * @param projectVo project vo
      * @param userName userName
      */
     void updateProject(ExchangisProjectInfo projectVo, String userName);

     /**
      * Query the page result
      * @param queryVo result vo
      * @return page result
      */
     PageResult<ExchangisProjectInfo> queryProjects(ProjectQueryVo queryVo);

     /**
      * Delete project
      * @param projectId project id
      */
     void deleteProject(Long projectId) throws ExchangisJobException;

     /**
      * Delete project by name
      * @param name
      */
     void deleteProjectByName(String name) throws ExchangisJobException;

     /**
      * Query the project detail
      * @param projectId project id
      * @return project vo
      */
     ExchangisProjectInfo getProjectDetailById(Long projectId);

     ExchangisProjectInfo getProjectById(Long projectId);

     ExchangisProjectInfo selectByName(String name);

     ExchangisProjectUser queryProjectUser(ExchangisProjectUserVo exchangisProjectUserVo);

}
