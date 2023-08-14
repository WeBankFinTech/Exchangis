package com.webank.wedatasphere.exchangis.job.server.utils;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.domain.OperationType;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author jefftlin
 * @Date 2022-09-15
 */
@Component
public class JobAuthorityUtils {

    static ProjectUserMapper projectUserMapper;
    static LaunchedJobDao launchedJobDao;
    static ExchangisJobEntityDao exchangisJobEntityDao;

    @Autowired
    public void setProjectUserMapper(ProjectUserMapper projectUserMapper) {
        JobAuthorityUtils.projectUserMapper = projectUserMapper;
    }

    @Autowired
    public void setLaunchedJobDao(LaunchedJobDao launchedJobDao) {
        JobAuthorityUtils.launchedJobDao = launchedJobDao;
    }

    @Autowired
    public void setExchangisJobEntityDao(ExchangisJobEntityDao exchangisJobEntityDao) {
        JobAuthorityUtils.exchangisJobEntityDao = exchangisJobEntityDao;
    }

    /**
     * @param privUser      privUser
     * @param projectId     project id
     * @param operationType enum("JOB_QUERY","JOB_ALTER","JOB_EXECUTE","JOB_RELEASE")
     * @return
     */
    public static boolean hasProjectAuthority(String privUser, Long projectId, OperationType operationType) throws ExchangisJobServerException {
        ExchangisProjectUser exchangisProjectUser = new ExchangisProjectUser();
        exchangisProjectUser.setProjectId(projectId);
        exchangisProjectUser.setPrivUser(privUser);
        exchangisProjectUser = projectUserMapper.queryProjectUser(exchangisProjectUser);
        if (Objects.isNull(exchangisProjectUser)) {
            String errorMsg = String.format("Project may be deleted, please check it with project_id [%s] and priv_user [%s] in table exchangis_project_user",
                    projectId, privUser);
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(), errorMsg);
        }
        if (StringUtils.isNotEmpty(privUser) &&
                Objects.nonNull(operationType)) {
            Integer privValue = exchangisProjectUser.getPriv();

            /**
             * view 4
             * edit 4+2=6
             * release 4+2+1=7
             */
            switch (operationType) {
                case JOB_QUERY:
                    return true;
                case JOB_ALTER:
                case JOB_EXECUTE:
                    return privValue >= 6;
                case JOB_RELEASE:
                    return privValue == 7;
                default:
                    throw new ExchangisJobServerException(ExchangisJobExceptionCode.UNSUPPORTED_OPERATION.getCode(), "Unsupported operationType");
            }
        }
        return false;
    }

    /**
     * @param privUser      privUser
     * @param jobId     job id
     * @param operationType enum("JOB_QUERY","JOB_ALTER","JOB_EXECUTE","JOB_RELEASE")
     * @return
     */
    public static boolean hasJobAuthority(String privUser, Long jobId, OperationType operationType) throws ExchangisJobServerException {
        ExchangisJobEntity exchangisBasicJob = exchangisJobEntityDao.getBasicInfo(jobId);
        if (Objects.isNull(exchangisBasicJob)) {
            String errorMsg = String.format("Job may be deleted, please check it with job_id [%s] in table exchangis_job_entity", jobId);
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(), errorMsg);
        }

        ExchangisProjectUser exchangisProjectUser = new ExchangisProjectUser();
        exchangisProjectUser.setProjectId(exchangisBasicJob.getProjectId());
        exchangisProjectUser.setPrivUser(privUser);
        exchangisProjectUser = projectUserMapper.queryProjectUser(exchangisProjectUser);
        if (Objects.isNull(exchangisProjectUser)) {
            String errorMsg = String.format("Project may be deleted, please check it with project_id [%s] and priv_user [%s] in table exchangis_project_user",
                    exchangisBasicJob.getProjectId(), privUser);
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(), errorMsg);
        }
        return hasProjectAuthority(privUser, exchangisProjectUser.getProjectId(), operationType);
    }

    public static boolean hasJobExecuteSituationAuthority(String privUser, String jobExecutionId, OperationType operationType) throws ExchangisJobServerException {
        LaunchedExchangisJobEntity launchedExchangisJob = launchedJobDao.searchLaunchedJob(jobExecutionId);
        if (Objects.isNull(launchedExchangisJob)) {
            String errorMsg = String.format("Luanched job may be deleted, please check it with job_execution_id [%s] in table exchangis_launched_job_entity",
                    jobExecutionId);
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(), errorMsg);
        }

        return hasJobAuthority(privUser, launchedExchangisJob.getJobId(), operationType);
    }
}
