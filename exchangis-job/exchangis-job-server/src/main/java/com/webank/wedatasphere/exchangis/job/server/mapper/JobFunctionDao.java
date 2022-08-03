package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.server.vo.JobFunction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author davidhua
 * 2020/4/23
 */
public interface JobFunctionDao {
    /**
     * List name referenced
     * @param type
     * @parm tabName
     * @return
     */
    List<String> listRefNames(@Param("tabName") String tabName, @Param("type") String type);

    /**
     * List function entities
     * @param type
     * @return
     */
    List<JobFunction> listFunctions(@Param("tabName") String tabName, @Param("type") String type);
}
