package com.webank.wedatasphere.exchangis.job.server.web;

import java.util.ArrayList;
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

import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
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
    public Message getJobList(@QueryParam(value = "projectId") long projectId,
        @QueryParam(value = "jobType") String jobType, @QueryParam(value = "name") String name) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, jobType, name);
        return Message.ok().data("result", joblist);
    }

    @GET
    @Path("/engineType")
    public Message getEngineList() {
        List<String> enginetypelist = new ArrayList<>();
        enginetypelist.add("datax");
        enginetypelist.add("sqoop");
        enginetypelist.add("distcopy");
        return Message.ok().data("result", enginetypelist);
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
