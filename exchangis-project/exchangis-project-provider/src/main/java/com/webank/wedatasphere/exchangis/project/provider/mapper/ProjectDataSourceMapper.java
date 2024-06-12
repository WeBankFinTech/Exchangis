package com.webank.wedatasphere.exchangis.project.provider.mapper;

import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDataSource;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/6/12
 */
public interface ProjectDataSourceMapper {

    /**
     * Get basic info by id
     * @param projectId project id
     * @return project entity
     */
    List<ExchangisProjectDataSource> getDataSourcesByProject(Long projectId);

    /**
     * Insert project
     * @param projectDataSources project datasource list
     * @return project id
     */
    int insertProjectDataSources(List<ExchangisProjectDataSource> projectDataSources);

    /**
     * Delete project datasource
     * @param projectId
     */
    void deleteProjectDataSource(Long projectId);
}
