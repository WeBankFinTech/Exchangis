package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.builder.DataXJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    public Message testbuildjob() {
        ExchangisJobBuilder jobbuilder = new DataXJobBuilder();
        ExchangisJob job = new ExchangisJob();
        job.setContent("[{\"subjobName\":\"subjob1\",\"dataSources\":{\"source_id\":\"HIVE.10001.db_test.table_test\",\"sink_id\":\"MYSQL.10002.db_mask.table_mask\"},\"params\":{\"sources\":[{\"config_key\":\"exchangis.job.hive.transform_type\",\"config_name\":\"传输方式\",\"config_value\":\"二进制\",\"sort\":1},{\"config_key\":\"exchangis.job.hive.partition\",\"config_name\":\"分区信息\",\"config_value\":\"2021-08-17\",\"sort\":2}],\"sinks\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}]},\"transforms\":{\"type\":\"SQL|MAPPING\",\"sql\":\"\",\"mapping\":[{\"source_field_name\":\"field1\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field2\",\"sink_field_type\":\"varchar\",\"validator\":[\">100\",\"<200\"],\"transformer\":{\"name\":\"ex_substr\",\"params\":[\"1\",\"3\"]}},{\"source_field_name\":\"field3\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field4\",\"sink_field_type\":\"varchar\",\"validator\":[\"like'%example'\"],\"transformer\":{\"name\":\"ex_replace\",\"params\":[\"1\",\"3\",\"***\"]}}]},\"settings\":[{\"config_key\":\"errorlimit_percentage\",\"config_name\":\"脏数据占比阈值\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"errorlimit_record\",\"config_name\":\"脏数据最大记录数\",\"config_value\":\"10\",\"sort\":2}]}]");
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

    @POST
    @Path("/{id}/save")
    public Message saveJobConfigAndSubjobs(@PathParam("id") Long id,
                                           @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.updateJob(exchangisJobContentDTO, id);
        return Message.ok().data("result", exchangisJob);
    }

}