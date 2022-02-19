package com.webank.wedatasphere.exchangis.job.server.restful;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskSpeedLimitVO;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * The basic controller of Exchangis job
 *
 */
@RestController
@RequestMapping(value = "exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobRestfulApi.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Autowired
    private ExchangisJobBuilderManager jobBuilderManager;

    @Autowired
    private ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @Autowired
    private ExchangisLaunchTaskMapper launchTaskMapper;

    @RequestMapping( value = "", method = RequestMethod.GET)
    public Message getJobList(@RequestParam(value = "projectId") Long projectId,
                              @RequestParam(value = "jobType", required = false) String jobType,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "current", required = false) int current,
                              @RequestParam(value = "size", required = false) int size) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, jobType, name, current, size);
        int total = exchangisJobService.count(projectId, jobType, name);
        //List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, jobType, name);
        Message message = Message.ok();
        message.data("total", total);
        message.data("result", joblist);
        return message;
        //return Message.ok().data("result", joblist);
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
        // TODO limit the engine type in exchangis
//        return Message.ok().data("result", EngineTypeEnum.values());
        return Message.ok().data("result", new EngineTypeEnum[]{EngineTypeEnum.SQOOP});
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

//    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.PUT)
//    public Message updateJobByDss(@PathVariable("nodeId") String nodeId, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
//        LOGGER.info("updateJobByDss nodeId {},ExchangisJobBasicInfoDTO {}", nodeId,exchangisJobBasicInfoDTO.toString());
//        ExchangisJobBasicInfoVO job = exchangisJobService.updateJobByDss(exchangisJobBasicInfoDTO, nodeId);
//        return Message.ok().data("result", job);
//    }

//    @RequestMapping( value = "/import", method = RequestMethod.POST, headers = {"content-type=multipart/form-data"})
//    public Message importSingleJob(@RequestPart("multipartFile") MultipartFile multipartFile) {
//        ExchangisJobBasicInfoVO job = exchangisJobService.importSingleJob(multipartFile);
//        return Message.ok().data("result", job);
//    }

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message deleteJob(@PathVariable("id") Long id) {
        exchangisJobService.deleteJob(id);
        return Message.ok("job deleted");
    }

//    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.DELETE)
//    public Message deleteJobByDss(@PathVariable("nodeId") String nodeId) {
//        LOGGER.info("deleteJobByDss nodeId {}", nodeId);
//        return Message.ok("job deleted");
//    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getJob(HttpServletRequest request, @PathVariable("id") Long id) throws ExchangisJobServerException {
        ExchangisJobVO job = exchangisJobService.getJob(request, id);
        return Message.ok().data("result", job);
    }

//    @RequestMapping( value = "/dss/{nodeId}", method = RequestMethod.GET)
//    public Message getJobByDssProject(HttpServletRequest request, @PathVariable("nodeId") String nodeId) throws ExchangisJobServerException {
//        LOGGER.info("getJobByDssProject nodeId {}", nodeId);
//        ExchangisJobVO job = exchangisJobService.getJobByDss(request, nodeId);
//        return Message.ok().data("result", job);
//    }



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

    @RequestMapping( value = "{id}/speedlimit/{task_name}/params/ui", method = RequestMethod.GET)
    public Message getSpeedLimitSettings(@PathVariable("id") Long id, @PathVariable("task_name") String taskName) {
        List<ElementUI<?>> speedLimitSettings = this.exchangisJobService.getSpeedLimitSettings(id, taskName);
        return Message.ok().data("ui", speedLimitSettings);
    }

    @RequestMapping( value = "{id}/speedlimit/{task_name}", method = RequestMethod.PUT)
    public Message setSpeedLimitSettings(@PathVariable("id") Long id, @PathVariable("task_name") String taskName,
                                         @RequestBody ExchangisTaskSpeedLimitVO settings) {
        this.exchangisJobService.setSpeedLimitSettings(id, taskName, settings);
        return Message.ok();
    }


}
