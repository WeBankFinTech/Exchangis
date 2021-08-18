package com.webank.wedatasphere.exchangis.job.server.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
 * @date 2021 /08/18
 * @since 2021 -08-10
 */
@RestController
@RequestMapping("exchangis/job")
public class ExchangisJobController {

    @Autowired
    private ExchangisJobService exchangisJobService;

    @GetMapping
    public Message getJobList(HttpServletRequest request, @RequestParam(value = "projectId", required = true) long projectId, @RequestParam(value = "type", required = true) String type, @RequestParam(value = "name", required = false) String name) {
        List<ExchangisJobBasicInfoVO> joblist = exchangisJobService.getJobList(projectId, type, name);
        return Message.ok().data("result", joblist);
    }

    @GetMapping("/engineType")
    public Message getEngineList(HttpServletRequest request) {
        List<String> enginetypelist = new ArrayList<>();
        enginetypelist.add("datax");
        enginetypelist.add("sqoop");
        enginetypelist.add("distcopy");
        return Message.ok().data("result", enginetypelist);
    }

    @PostMapping
    public Message createJob(HttpServletRequest request,
                             @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.createJob(exchangisJobBasicInfoDTO);
        return Message.ok().data("result", job);
    }

    @PostMapping("/{sourceJobId}/copy")
    public Message copyJob(HttpServletRequest request, @PathVariable Long sourceJobId, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) throws Exception {
        ExchangisJobBasicInfoVO job = exchangisJobService.copyJob(exchangisJobBasicInfoDTO, sourceJobId);
        return Message.ok().data("result", job);
    }

    @PutMapping("/{id}")
    public Message updateJob(HttpServletRequest request, @PathVariable Long id, @RequestBody ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJobBasicInfoVO job = exchangisJobService.updateJob(exchangisJobBasicInfoDTO, id);
        return Message.ok().data("result", job);

    }

    @PostMapping("/import")
    public Message importSingleJob(HttpServletRequest request, @RequestPart("multipartFile") MultipartFile multipartFile) {
        ExchangisJobBasicInfoVO job = exchangisJobService.importSingleJob(multipartFile);
        return Message.ok().data("result", job);
    }

    @DeleteMapping("/{id}")
    public Message deleteJob(HttpServletRequest request, @PathVariable Long id) {
        exchangisJobService.deleteJob(id);
        return Message.ok("job deleted");
    }

    @GetMapping("/{id}")
    public Message getJob(@PathVariable Long id) throws ExchangisJobErrorException {
        ExchangisJob job = exchangisJobService.getJob(id);
        return Message.ok().data("result", job);
    }

    @PostMapping("/{id}/save")
    public Message saveJobConfigAndSubjobs(@PathVariable Long id,
        @RequestBody ExchangisJobContentDTO exchangisJobContentDTO) throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.updateJob(exchangisJobContentDTO, id);
        return Message.ok().data("result", exchangisJob);
    }

}
