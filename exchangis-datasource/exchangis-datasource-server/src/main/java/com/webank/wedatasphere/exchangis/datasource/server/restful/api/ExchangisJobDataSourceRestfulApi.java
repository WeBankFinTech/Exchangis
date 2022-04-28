package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import org.apache.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

// TODO 这里仅仅为了测试，JOB的接口在另外的 Controller 中
@RestController
@RequestMapping(value = "dss/exchangis/main", produces = {"application/json;charset=utf-8"})
public class ExchangisJobDataSourceRestfulApi {

    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisJobDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // 根据 任务ID 获取该任务的数据源所有配置项 UI 数据
    @RequestMapping( value = "jobs/{jobId}/datasource/ui", method = RequestMethod.GET)
    public Message getJobDataSourcesUI(HttpServletRequest request, @PathVariable("jobId")Long jobId) {
//        ExchangisDataSourceUIViewer jobDataSourceUI = this.exchangisDataSourceService.getJobDataSourceUIs(jobId);
        List<ExchangisDataSourceUIViewer> ui = this.exchangisDataSourceService.getJobDataSourceUIs(request, jobId);
        return Message.ok().data("ui", ui);
    }

    // 根据 任务引擎类型 获取该引擎的配置项 UI 数据
    @RequestMapping( value = "jobs/engine/{engineType}/settings/ui", method = RequestMethod.GET)
    public Message getJobEngineSettingsUI(HttpServletRequest request, @PathVariable("engineType")String engineType, @RequestParam(required = false)String labels) {
        List<ElementUI<?>> jobSettingsUI = this.exchangisDataSourceService.getJobEngineSettingsUI(engineType);
        return Message.ok().data("ui", jobSettingsUI);
    }

    // 根据 任务ID 获取该任务的数据源配置项 UI 数据
    @RequestMapping( value = "jobs/{jobId}/datasource/params/ui", method = RequestMethod.GET)
    public Message getJobDataSourceParamsUI(HttpServletRequest request, @PathVariable("jobId")Long jobId) {
        return this.exchangisDataSourceService.getJobDataSourceParamsUI(jobId);
    }

    // 根据 任务ID 获取该任务的数据源字段映射 UI 数据
    @RequestMapping( value = "jobs/{jobId}/datasource/transforms/ui", method = RequestMethod.GET)
    public Message getJobTransformsUI(HttpServletRequest request, @PathVariable("jobId")Long jobId) {
        return this.exchangisDataSourceService.getJobDataSourceTransformsUI(jobId);
    }

    // 根据 任务ID 获取该任务的数据源引擎配置项 UI 数据
    @RequestMapping( value = "jobs/{jobId}/{jobName}/datasource/settings/ui", method = RequestMethod.GET)
    public Message getJobSettingsUI(HttpServletRequest request, @PathVariable("jobId")Long jobId,
                                    @PathVariable("jobName")String jobName) throws Exception {
        return this.exchangisDataSourceService.getJobDataSourceSettingsUI(jobId, jobName);
    }
}
