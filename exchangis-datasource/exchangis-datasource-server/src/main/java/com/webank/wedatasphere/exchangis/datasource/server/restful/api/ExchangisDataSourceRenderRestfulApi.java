package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceRenderService;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

/**
 * Expose the ui interface to front-end rendering
 */
@RestController
@RequestMapping(value = "dss/exchangis/main/datasources/render", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceRenderRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceRenderRestfulApi.class);

    @Resource
    private DataSourceRenderService renderService;

    @RequestMapping(value = "/partition/element/{elementType:\\w+}", method = RequestMethod.GET)
    public Message partition(@PathVariable("elementType") String type,
                             @RequestParam("dataSourceId") Long dataSourceId,
                             @RequestParam("database") String database,
                             @RequestParam("table") String table, HttpServletRequest request){
        String userName = UserUtils.getLoginUser(request);
        ElementUI.Type uiType;
        try {
            uiType = ElementUI.Type.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (Exception e){
            return Message.error("Element type: [" + type +"] is not support (不兼容的元素类型)");
        }
        Message result = Message.ok();
        try{
            ElementUI<?> elementUI = renderService.getPartitionAndRender(userName, dataSourceId, database, table, uiType);
            result.data("type", uiType.name());
            if (Objects.nonNull(elementUI)){
                result.data("render", elementUI.getValue());
            }
        }catch(Exception e){
            String uiMessage = "Load to render partition info Failed (加载渲染分区信息失败)";
            LOG.error(uiMessage + ", reason: " + e.getMessage(), e);
            result = Message.error(uiMessage);
        }
        result.setMethod("/api/rest_j/v1/dss/exchangis/main/datasources/render/partition/element/" + type);
        return result;
    }

}
