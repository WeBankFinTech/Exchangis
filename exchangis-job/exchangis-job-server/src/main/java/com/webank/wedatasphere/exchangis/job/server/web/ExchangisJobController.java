package com.webank.wedatasphere.exchangis.job.server.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.launcher.builder.ExchangisLauncherJob;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.ExchangisTransformJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.linkis.computation.client.LinkisJobMetrics;
import com.webank.wedatasphere.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * The type Exchangis job controller.
 *
 * @author yuxin.yuan
 * @date 2021/08/18
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis/job")
public class ExchangisJobController {

    @Autowired
    private ExchangisJobService exchangisJobService;

    @Autowired
    private ExchangisJobBuilderManager jobBuilderManager;

    @Autowired
    private ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    private ExchangisJobLaunchManager<ExchangisLauncherJob> jobLaunchManager;

    @Autowired
    private ExchangisLaunchTaskMapper launchTaskMapper;

    private final ObjectWriter writer = new ObjectMapper().writer();

    @GET
    public Message getJobList(@QueryParam(value = "projectId") long projectId,
                              @QueryParam(value = "jobType") String jobType, @QueryParam(value = "name") String name) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, jobType, name);
        return Message.ok().data("result", joblist);
    }

    @GET
    @Path("/engineType")
    public Message getEngineList() {
        return Message.ok().data("result", EngineTypeEnum.values());
    }

    @POST
    public Message createJob(@Context HttpServletRequest request, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.createJob(request, exchangisJobBasicInfoDTO);
        return Message.ok().data("result", job);
    }

    @POST
    @Path("/{sourceJobId}/copy")
    public Message copyJob(@PathParam("sourceJobId") Long sourceJobId,
                           @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.copyJob(exchangisJobBasicInfoDTO, sourceJobId);
        return Message.ok().data("result", job);
    }

    @PUT
    @Path("/{id}")
    public Message updateJob(@PathParam("id") Long id, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.updateJob(exchangisJobBasicInfoDTO, id);
        return Message.ok().data("result", job);
    }

    @POST
    @Path("/import")
    public Message importSingleJob(@RequestPart("multipartFile") MultipartFile multipartFile) {
        ExchangisJobBasicInfoVO job = exchangisJobService.importSingleJob(multipartFile);
        return Message.ok().data("result", job);
    }

    @DELETE
    @Path("/{id}")
    public Message deleteJob(@PathParam("id") Long id) {
        exchangisJobService.deleteJob(id);
        return Message.ok("job deleted");
    }

    @GET
    @Path("/{id}")
    public Message getJob(@Context HttpServletRequest request, @PathParam("id") Long id) throws ExchangisJobErrorException {
        ExchangisJob job = exchangisJobService.getJob(request, id);
        return Message.ok().data("result", job);
    }

    @PUT
    @Path("/{id}/config")
    public Message saveJobConfig(@PathParam("id") Long id,
                                 @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.updateJobConfig(exchangisJobContentDTO, id);
        return Message.ok().data("result", exchangisJob);
    }

    @PUT
    @Path("/{id}/content")
    public Message saveSubjobs(@PathParam("id") Long id,
                               @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException, ExchangisDataSourceException {
        ExchangisJob exchangisJob = exchangisJobService.updateJobContent(exchangisJobContentDTO, id);
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


    @POST
    @Path("/{id}/action/execute")
    public Message executeJob(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        ExchangisJob job = exchangisJobService.getById(id);
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.putEnv("USER_NAME", userName);

        ctx.setOriginalJob(job);
        // ExchangisJob -> ExchangisTransformJob(SubExchangisJob)
        ExchangisTransformJob transformJob = jobBuilderManager.doBuild(job, ExchangisTransformJob.class, ctx);
        List<ExchangisEngineJob> engineJobs = new ArrayList<>();
        // ExchangisTransformJob(SubExchangisJob) -> List<ExchangisEngineJob>
        for (SubExchangisJob subExchangisJob : transformJob.getSubJobSet()) {
            String sourceDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE).get("datasource").getValue().toString();
            String sinkDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK).get("datasource").getValue().toString();
            if (!ctx.containsDatasourceParam(sourceDsId)) {
                Map<String, Object> sourceDsParam = this.exchangisDataSourceService.getDataSource(userName, Long.parseLong(sourceDsId)).getData().getInfo().getConnectParams();
                ctx.putDatasourceParam(sourceDsId, sourceDsParam);
            }
            if (!ctx.containsDatasourceParam(sinkDsId)) {
                Map<String, Object> sinkDsParam = this.exchangisDataSourceService.getDataSource(userName, Long.parseLong(sinkDsId)).getData().getInfo().getConnectParams();
                ctx.putDatasourceParam(sinkDsId, sinkDsParam);
            }
            // connectParams
            Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
                    SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
        }
        //  List<ExchangisEngineJob> -> List<ExchangisLauncherJob>
        List<ExchangisLauncherJob> launcherJobs = new ArrayList<>();
        for (ExchangisEngineJob engineJob : engineJobs) {
            Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
                    ExchangisEngineJob.class, ExchangisLauncherJob.class, ctx)).ifPresent(launcherJobs::add);
        }
        if (launcherJobs.isEmpty()) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.JOB_BUILDER_ERROR.getCode(),
                    "The result set of launcher job is empty, please examine your job entity, [ 生成LauncherJob为空 ]", null);
        }

        for (ExchangisLauncherJob launcherJob : launcherJobs) {
            String launchName = launcherJob.getLaunchName();
            ExchangisJobLauncher<ExchangisLauncherJob> jobLauncher = this.jobLaunchManager.getJoblauncher("Linkis");
            SubmittableSimpleOnceJob launch = jobLauncher.launch(launcherJob);

            String launchId = launch.getId();
            String status = launch.getStatus();

            LinkisJobMetrics jobMetrics = launch.getJobMetrics();

            ExchangisLaunchTask task = new ExchangisLaunchTask();
            task.setTaskName(launcherJob.getTaskName());
            task.setJobId(launcherJob.getId());
            task.setJobName(launcherJob.getJobName());

            try {
                String content = writer.writeValueAsString(launcherJob.getJobContent());
                task.setContent(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                task.setContent("{}");
            }

            task.setCreateTime(new Date());
            task.setCreateUser(launcherJob.getCreateUser());
            task.setLaunchTime(new Date());
            task.setProxyUser(launcherJob.getProxyUser());

            try {
                String runtimes = writer.writeValueAsString(launcherJob.getRuntimeMap());
                task.setParamsJson(runtimes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                task.setParamsJson("{}");
            }

            task.setLaunchId(launchId);
            task.setStatus(status);
            task.setEngineType(launcherJob.getEngine());

            this.launchTaskMapper.insert(task);

            launch.waitForCompleted();

            task.setStatus(launch.getStatus());
            task.setCompleteTime(new Date());

            this.launchTaskMapper.updateById(task);

        }
        return Message.ok();
    }

//    @GET
//    @Path("{id}/speedlimit/{task_name}/params/ui")
//    public Response getSpeedLimitSettings(@PathParam("id") Long id, @PathParam("task_name") String taskName) {
//        List<ElementUI> speedLimitSettings = this.exchangisJobService.getSpeedLimitSettings(id, taskName);
//        Message message = Message.ok().data("ui", speedLimitSettings);
//        return Message.messageToResponse(message);
//    }

//    @PUT
//    @Path("{id}/speedlimit/{task_name}")
//    public Response setSpeedLimitSettings(@PathParam("id") Long id, @PathParam("task_name") String taskName, @RequestBody ExchangisTaskSpeedLimitVO settings) {
//        this.exchangisJobService.setSpeedLimitSettings(id, taskName, settings);
//        return Message.messageToResponse(Message.ok());
//    }


}
