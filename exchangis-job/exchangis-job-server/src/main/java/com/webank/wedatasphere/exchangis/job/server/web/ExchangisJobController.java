package com.webank.wedatasphere.exchangis.job.server.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.entity.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskSpeedLimitVO;
import org.apache.linkis.computation.client.LinkisJobMetrics;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SINK;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SOURCE;

/**
 * The type Exchangis job controller.
 *
 * @author yuxin.yuan
 * @date 2021/08/18
 */
@RestController
@RequestMapping(value = "exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisJobController.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Autowired
    private ExchangisJobBuilderManager jobBuilderManager;

    @Autowired
    private ExchangisDataSourceService exchangisDataSourceService;

//    @Autowired
//    private ExchangisTaskLaunchManager jobLaunchManager;

    @Autowired
    private ExchangisLaunchTaskMapper launchTaskMapper;

    private final ObjectWriter writer = new ObjectMapper().writer();

    @RequestMapping( value = "", method = RequestMethod.GET)
    public Message getJobList(@RequestParam(value = "projectId") Long projectId,
                              @RequestParam(value = "jobType", required = false) String jobType,
                              @RequestParam(value = "name", required = false) String name) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, jobType, name);
        return Message.ok().data("result", joblist);
    }

    @RequestMapping( value = "/dss", method = RequestMethod.GET)
    public Message getJobListByDss(@RequestParam(value = "dssProjectId") Long dssProjectId,
                                   @RequestParam(value = "jobType", required = false) String jobType,
                                   @RequestParam(value = "name", required = false) String name) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobListByDssProject(dssProjectId, jobType, name);
        return Message.ok().data("result", joblist);
    }

    @RequestMapping( value = "/engineType", method = RequestMethod.GET)
    public Message getEngineList() {
        return Message.ok().data("result", EngineTypeEnum.values());
    }

    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message createJob(HttpServletRequest request, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.createJob(request, exchangisJobBasicInfoDTO);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/{sourceJobId}/copy", method = RequestMethod.POST)
    public Message copyJob(@PathVariable("sourceJobId") Long sourceJobId,
                           @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.copyJob(exchangisJobBasicInfoDTO, sourceJobId);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT)
    public Message updateJob(@PathVariable("id") Long id, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.updateJob(exchangisJobBasicInfoDTO, id);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.PUT)
    public Message updateJobByDss(@PathVariable("nodeId") String nodeId, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        LOGGER.info("updateJobByDss nodeId {},ExchangisJobBasicInfoDTO {}", nodeId,exchangisJobBasicInfoDTO.toString());
        ExchangisJobBasicInfoVO job = exchangisJobService.updateJobByDss(exchangisJobBasicInfoDTO, nodeId);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/import", method = RequestMethod.POST, headers = {"content-type=multipart/form-data"})
    public Message importSingleJob(@RequestPart("multipartFile") MultipartFile multipartFile) {
        ExchangisJobBasicInfoVO job = exchangisJobService.importSingleJob(multipartFile);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message deleteJob(@PathVariable("id") Long id) {
        exchangisJobService.deleteJob(id);
        return Message.ok("job deleted");
    }

    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.DELETE)
    public Message deleteJobByDss(@PathVariable("nodeId") String nodeId) {
        LOGGER.info("deleteJobByDss nodeId {}", nodeId);
        return Message.ok("job deleted");
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getJob(HttpServletRequest request, @PathVariable("id") Long id) throws ExchangisJobServerException {
        ExchangisJobVO job = exchangisJobService.getJob(request, id);
        return Message.ok().data("result", job);
    }

    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.GET)
    public Message getJobByDssProject(HttpServletRequest request, @PathVariable("nodeId") String nodeId) throws ExchangisJobServerException {
        LOGGER.info("getJobByDssProject nodeId {}", nodeId);
        ExchangisJobVO job = exchangisJobService.getJobByDss(request, nodeId);
        return Message.ok().data("result", job);
    }



    @RequestMapping( value = "/{id}/config", method = RequestMethod.PUT)
    public Message saveJobConfig(@PathVariable("id") Long id,
                                 @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobServerException {
        ExchangisJobVO exchangisJob = exchangisJobService.updateJobConfig(exchangisJobContentDTO, id);
        return Message.ok().data("result", exchangisJob);
    }

    @RequestMapping( value = "/{id}/content", method = RequestMethod.PUT)
    public Message saveSubjobs(@PathVariable("id") Long id,
                               @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobServerException, ExchangisDataSourceException {
        ExchangisJobVO exchangisJob = exchangisJobService.updateJobContent(exchangisJobContentDTO, id);
        return Message.ok().data("result", exchangisJob);
    }

//    @POST
//    @Path("/{id}/save")
//    public Message saveJobConfigAndSubjobs(@PathParam("id") Long id,
//                                           @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException {
//        ExchangisJob exchangisJob = exchangisJobService.updateJob(exchangisJobContentDTO, id);
//        return Message.ok().data("result", exchangisJob);
//    }

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;


    @RequestMapping( value = "/{id}/action/execute", method = RequestMethod.POST)
    public Message executeJob(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
//        String userName = SecurityFilter.getLoginUsername(request);
//        ExchangisJobInfo job = new ExchangisJobInfo(exchangisJobService.getById(id));
//        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
//        ctx.putEnv("USER_NAME", userName);
//
//        ctx.setOriginalJob(job);
//        // ExchangisJob -> ExchangisTransformJob(SubExchangisJob)
//        TransformExchangisJob transformJob = jobBuilderManager.doBuild(job, TransformExchangisJob.class, ctx);
//        List<ExchangisEngineJob> engineJobs = new ArrayList<>();
//        // ExchangisTransformJob(SubExchangisJob) -> List<ExchangisEngineJob>
//        for (SubExchangisJob subExchangisJob : transformJob.getSubJobSet()) {
//            String sourceDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE).get("datasource").getValue().toString();
//            String sinkDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK).get("datasource").getValue().toString();
//            if (!ctx.containsDatasourceParam(sourceDsId)) {
//                String type = subExchangisJob.getSourceType();
//                String database = subExchangisJob.getRealmParams(REALM_JOB_CONTENT_SOURCE).get("database").getValue().toString();
//                String table = subExchangisJob.getRealmParams(REALM_JOB_CONTENT_SOURCE).get("table").getValue().toString();
//                Map<String, Object> sourceDsParam = this.exchangisDataSourceService.getMetadata(userName, Long.parseLong(sourceDsId), type, database, table);
//                ctx.putDatasourceParam(sourceDsId, sourceDsParam);
//            }
//            if (!ctx.containsDatasourceParam(sinkDsId)) {
//                String type = subExchangisJob.getSourceType();
//                String database = subExchangisJob.getRealmParams(REALM_JOB_CONTENT_SINK).get("database").getValue().toString();
//                String table = subExchangisJob.getRealmParams(REALM_JOB_CONTENT_SINK).get("table").getValue().toString();
//                Map<String, Object> sinkDsParam = this.exchangisDataSourceService.getMetadata(userName, Long.parseLong(sinkDsId), type, database, table);
//                ctx.putDatasourceParam(sinkDsId, sinkDsParam);
//            }
//            // connectParams
//            Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
//                    SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
//        }
//        // List<ExchangisEngineJob> -> List<ExchangisLauncherJob>
//        List<LaunchableExchangisTask> launcherJobs = new ArrayList<>();
//        for (ExchangisEngineJob engineJob : engineJobs) {
//            Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
//                    ExchangisEngineJob.class, LaunchableExchangisTask.class, ctx)).ifPresent(launcherJobs::add);
//        }
//        if (launcherJobs.isEmpty()) {
//            throw new ExchangisJobException(ExchangisJobExceptionCode.TASK_BUILDER_ERROR.getCode(),
//                    "The result set of launcher job is empty, please examine your job entity, [ 生成LauncherJob为空 ]", null);
//        }
//
//        List<ExchangisLaunchTask> tasks = new ArrayList<>();
//
//
//        for (LaunchableExchangisTask launcherJob : launcherJobs) {
//            ExchangisTaskLauncher<LaunchableExchangisTask> jobLauncher = this.jobLaunchManager.getTaskLauncher("Linkis");
//            SubmittableSimpleOnceJob launch = jobLauncher.launch(launcherJob);
//
//            String launchId = launch.getId();
//            String status = launch.getStatus();
//
//            LinkisJobMetrics jobMetrics = launch.getJobMetrics();
//
//            ExchangisLaunchTask task = new ExchangisLaunchTask();
//            // TODO set task name
////            task.setTaskName(launcherJob.getTaskName());
//            task.setJobId(launcherJob.getId());
//            task.setJobName(launcherJob.getName());
//
//            try {
//                String content = writer.writeValueAsString(launcherJob.getLinkisJobContent());
//                task.setContent(content);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//                task.setContent("{}");
//            }
//
//            task.setCreateTime(new Date());
//            task.setCreateUser(launcherJob.getExecuteUser());
//            task.setLaunchTime(new Date());
//            task.setProxyUser(launcherJob.getExecuteUser());
//
//            try {
//                String runtimes = writer.writeValueAsString(launcherJob.getLinkisParamsMap());
//                task.setParamsJson(runtimes);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//                task.setParamsJson("{}");
//            }
//
//            task.setLaunchId(launchId);
//            task.setStatus(status);
//            task.setEngineType(launcherJob.getEngineType());
//
//            this.launchTaskMapper.insert(task);
//
////            launch.waitForCompleted();
////
////            task.setStatus(launch.getStatus());
////            task.setCompleteTime(new Date());
////
////            this.launchTaskMapper.updateById(task);
//
//            tasks.add(task);
//
//        }
//        return Message.ok().data("tasks", tasks);
        return Message.ok().data("tasks", new ArrayList<>());
    }

    @RequestMapping( value = "{id}/speedlimit/{task_name}/params/ui", method = RequestMethod.GET)
    public Message getSpeedLimitSettings(@PathVariable("id") Long id, @PathVariable("task_name") String taskName) {
        List<ElementUI> speedLimitSettings = this.exchangisJobService.getSpeedLimitSettings(id, taskName);
        return Message.ok().data("ui", speedLimitSettings);
    }

    @RequestMapping( value = "{id}/speedlimit/{task_name}", method = RequestMethod.PUT)
    public Message setSpeedLimitSettings(@PathVariable("id") Long id, @PathVariable("task_name") String taskName,
                                         @RequestBody ExchangisTaskSpeedLimitVO settings) {
        this.exchangisJobService.setSpeedLimitSettings(id, taskName, settings);
        return Message.ok();
    }


}
