package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.datasource.core.domain.*;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelTypeKeyService;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "dss/exchangis/main/datasources", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceModelTypeKeyController {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceModelTypeKeyController.class);

    @Resource
    private DataSourceModelTypeKeyService dataSourceModelTypeKeyService;

    @Resource
    private RateLimitService rateLimitService;

    @RequestMapping( value = "/model/keyDefine", method = RequestMethod.GET)
    public Message selectAllKeyDefines(HttpServletRequest request) {
        String username = UserUtils.getLoginUser(request);
        DataSourceModelTypeKeyQuery pageQuery = new DataSourceModelTypeKeyQuery();
        try {
            List<DataSourceModelTypeKey> keyDefines = dataSourceModelTypeKeyService.selectAllDsModelTypeKeys(pageQuery);
            Message message = Message.ok();
            message.data("list", keyDefines);
            if (!keyDefines.isEmpty()) {
                message.data("total", keyDefines.size());
            }
            return message;
        } catch (Exception t) {
            LOG.error("Failed to query key defines");
            return Message.error("Failed to query key defines (获取数据源模板类型属性定义失败) " + t.getMessage());
        }
    }

}
