package com.webank.wedatasphere.exchangis.job.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;

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
    @Autowired
    private ExchangisJobService exchangisJobService;

    @GetMapping
    public String test(){
        return "hello";
    }
}
