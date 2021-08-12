package com.webank.wedatasphere.exchangis.job.server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@RestController
@RequestMapping("/server/exchangisJob")
public class ExchangisJobController {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ExchangisJobService exchangisJobService;

    @GetMapping
    public String test(){
        return "hello";
    }

    @GetMapping("/job")
    public Response getJobList() {
        return Message.messageToResponse(Message.ok());
    }

    @GetMapping("/engineType")
    public Response getEngineList() {
        Message message = new Message();
        message.setMessage("datax");
        return Message.messageToResponse(message);
    }

    @PostMapping("/job")
    public Response createJob(@Context HttpServletRequest request, @RequestBody ExchangisJob body) throws Exception{
        ExchangisJob job = mapper.readValue(mapper.writeValueAsString(body), ExchangisJob.class);
        return Message.messageToResponse(new Message().setMessage(job.toString()));
    }

    @PostMapping("/job/{sourceJobId}/copy")
    public Response copyJob(@PathVariable Long sourceJobId, @RequestBody @NotNull ExchangisJob body) throws Exception{
        ExchangisJob job = mapper.readValue(mapper.writeValueAsString(body), ExchangisJob.class);
        return Message.messageToResponse(new Message().setMessage(job.toString()));
    }

    @PutMapping("/job/{id}")
    public String updateJob(@PathVariable Long id, @RequestBody @NotNull ExchangisJob body) {
        return "test";
    }

    @PostMapping("/job/import")
    public String importSingleJob(@RequestBody @NotNull ExchangisJob body) {
        return "test";
    }

    @DeleteMapping("/job/{id}")
    public String deleteJob(@PathVariable Long id) {
        return "test";
    }

    @GetMapping("/job/{id}")
    public String getSingleJobInfo(@PathVariable Long id) {
        return "test";
    }
}
