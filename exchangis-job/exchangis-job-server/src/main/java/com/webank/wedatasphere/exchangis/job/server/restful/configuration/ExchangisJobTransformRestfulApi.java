package com.webank.wedatasphere.exchangis.job.server.restful.configuration;

import com.google.inject.spi.Message;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRequestVo;
import com.webank.wedatasphere.exchangis.job.server.service.JobTransformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "dss/exchangis/main/job/transform", produces = {"application/json;charset=utf-8"})
public class ExchangisJobTransformRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobTransformRestfulApi.class);

    @Resource
    private JobTransformService transformService;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public Message settings(HttpServletRequest request, @RequestBody TransformRequestVo params){
        return null;
    }
}
