package com.webank.wedatasphere.exchangis.job.server.web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.launcher.linkis.LinkisExchangisJobLanuncher;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.linkis.server.Message;

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

    @GET
    @Path("/testbuildjob")
    public Message testbuildjob() throws Exception {
        ExchangisJobBuilder jobbuilder = ExchangisJobBuilderManager.getJobBuilder(EngineTypeEnum.DATAX);
        ExchangisJob job = new ExchangisJob();
        job.setContent("[{\"subjobName\":\"subjob1\",\"dataSources\":{\"source_id\":\"MYSQL.10002.db_mask.table_source\",\"sink_id\":\"MYSQL.10002.db_mask.table_sink\"},\"params\":{\"sources\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}],\"sinks\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}]},\"transforms\":{\"type\":\"MAPPING\",\"sql\":\"\",\"mapping\":[{\"source_field_name\":\"field1\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field2\",\"sink_field_type\":\"varchar\",\"validator\":[\">100\",\"<200\"],\"transformer\":{\"name\":\"ex_substr\",\"params\":[\"1\",\"3\"]}},{\"source_field_name\":\"field3\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field4\",\"sink_field_type\":\"varchar\",\"validator\":[\"like'%example'\"],\"transformer\":{\"name\":\"ex_replace\",\"params\":[\"1\",\"3\",\"***\"]}}]},\"settings\":[{\"config_key\":\"errorlimit_percentage\",\"config_name\":\"脏数据占比阈值\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"errorlimit_record\",\"config_name\":\"脏数据最大记录数\",\"config_value\":\"10\",\"sort\":2}]}]");
        job.setEngineType("datax");
        job.setCreateUser("IDE");
        job.setProxyUser("hadoop");
        job.setId(1L);
        jobbuilder.buildJob(job);

        return Message.ok().data("result", EngineTypeEnum.values());
    }

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
    public Message createJob(@RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.createJob(exchangisJobBasicInfoDTO);
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
    public Message getJob(@PathParam("id") Long id) throws ExchangisJobErrorException {
        ExchangisJob job = exchangisJobService.getJob(id);
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
                               @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException {
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
    public Message executeJob(@PathParam("id") Long id) throws Exception {
        ExchangisJob job = exchangisJobService.getById(id);
        ExchangisJobBuilder jobBuiler =
            ExchangisJobBuilderManager.getJobBuilder(EngineTypeEnum.valueOf(job.getEngineType()));
        List<ExchangisLaunchTask> exchangisLaunchTasks = jobBuiler.buildJob(job);

        exchangisLaunchTaskService.saveBatch(exchangisLaunchTasks);

        LinkisExchangisJobLanuncher jobLanuncher = new LinkisExchangisJobLanuncher();
        exchangisLaunchTasks.forEach(launchTask -> jobLanuncher.launch(launchTask));
        return Message.ok();
    }

}
