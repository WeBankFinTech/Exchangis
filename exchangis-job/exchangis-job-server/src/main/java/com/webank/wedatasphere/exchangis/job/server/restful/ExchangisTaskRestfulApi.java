package com.webank.wedatasphere.exchangis.job.server.restful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * The type Exchangis task controller.
 *
 * @date 2021/10/13
 */
@RestController
@RequestMapping(value = "dss/exchangis/main/tasks", produces = {"application/json;charset=utf-8"})
public class ExchangisTaskRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisTaskRestfulApi.class);

}
