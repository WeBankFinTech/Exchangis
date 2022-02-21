package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.builder.ElementUIFactory;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceRenderService;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Expose the ui interface to front-end rendering
 */
@RestController
@RequestMapping(value = "exchangis/datasources/render")
public class ExchangisDataSourceRenderRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceRenderRestfulApi.class);

    @Resource
    private DataSourceRenderService renderService;

    @RequestMapping(value = "/partition/element/{elementType:\\w+}/")
    public Message partition(@PathVariable("elementType") String type,
                             @RequestParam("dataSourceId") Long dataSourceId,
                             @RequestParam("database") String database,
                             @RequestParam("table") String table, HttpServletRequest request){
        String userName = SecurityFilter.getLoginUsername(request);
        ElementUI.Type uiType;
        try {
            uiType = ElementUI.Type.valueOf(type);
        } catch (Exception e){
            return Message.error("Element type: [" + type +"] is not support (不兼容的元素类型)");
        }
        try{
            ElementUI<?> elementUI = renderService.getPartitionAndRender(userName, dataSourceId, database, table, uiType);

        }catch(Exception e){

        }
        return null;
    }

}
