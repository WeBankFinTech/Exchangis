package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.enums.AuthType;
import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.datasource.core.domain.*;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelTypeKeyService;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.webank.wedatasphere.exchangis.common.Constants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model configuration management
 */
@RestController
@RequestMapping(value = "dss/exchangis/main/datasources/model", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceModelController {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceModelController.class);

    @Resource
    private DataSourceModelService dataSourceModelService;

    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private DataSourceModelTypeKeyService dataSourceModelTypeKeyService;

    public ExchangisDataSourceModelController(DataSourceModelService dataSourceModelService) {
        this.dataSourceModelService = dataSourceModelService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Message add(@Valid DataSourceModel model, HttpServletRequest request) {
        if (StringUtils.isNotBlank(model.getCreateUser()) && isDuplicate(model.getModelName())) {
            return Message.error("The name of model already exists");
        }
        String userName = UserUtils.getLoginUser(request);
        if (GlobalConfiguration.isAdminUser(userName)) {
            model.setCreateOwner("");
        } else {
            model.setCreateOwner(model.getCreateUser());
        }
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));
        boolean ok = dataSourceModelService.add(model);
        return ok ? Message.ok() :
                Message.error("Failed ti add datasource model");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Message update(@PathVariable Long id, @Valid DataSourceModel model, HttpServletRequest request) {
        if (id <= 0) {
            Message.error("Error dataSource model");
        }
        model.setId(id);
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));

        // TODO Datasource model post processor, transactional with insert operate
        try {
            dataSourceModelService.update(model);
        } catch (RuntimeException e) {
            return Message.error("Failed to update the dataSource model, cause by : " + e.getMessage());
        }
        return Message.ok();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(@PathVariable Long id, HttpServletRequest request) {
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelId(id);
        boolean result = dataSourceModelService.delete(id);
        // TODO Datasource model post processor

        return result ? Message.ok() :
                Message.error("Failed to delete the dataSource model");
    }

    @RequestMapping(value = "/{modelType:\\w+}/list", method = RequestMethod.GET)
    public Message listByType(@PathVariable("modelType") String modelType,
                              HttpServletRequest request) {
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setSourceType(modelType);
        List<DataSourceModel> list = dataSourceModelService.selectAllList(query);
        List<ModelTemplateStructure> structureList = list.stream().map(modelAssembly -> {
            ModelTemplateStructure structure = new ModelTemplateStructure();
            structure.setModelId(modelAssembly.getId());
            structure.setModelName(modelAssembly.getModelName());
            Map<String, Object> params = modelAssembly.resolveParams();
            structure.setAuthType(
                    String.valueOf(params.getOrDefault(Constants.PARAM_AUTH_TYPE, "")));
            return structure;
        }).collect(Collectors.toList());
        return Message.ok().data("data", structureList);
    }

    @RequestMapping(value = "pageList", method = RequestMethod.GET)
    public Message pageList(DataSourceModelQuery pageQuery, HttpServletRequest request) {
        PageList<DataSourceModel> list = null;
        int pageSize = pageQuery.getPageSize();
        if (pageSize == 0) {
            pageQuery.setPageSize(10);
        }
        list = dataSourceModelService.findPage(pageQuery);
        String username = UserUtils.getLoginUser(request);
        list.getData().forEach(element -> {
            //Bind authority scopes
            if (StringUtils.isNotBlank(element.getCreateUser()) && !element.getCreateUser().equals(username)) {
                //Remove sensitive data
                Map<String, Object> params = element.resolveParams();
                Map<String, Object> newParams = new HashMap<>();
                newParams.put(Constants.PARAM_AUTH_TYPE, params.getOrDefault(Constants.PARAM_AUTH_TYPE, AuthType.NONE));
                element.setParameter(Json.toJson(newParams, null));
            }
        });
        return Message.ok().data("data", list);
    }

    @RequestMapping(value = "selectAll", method = RequestMethod.GET)
    public Message selectAll(DataSourceModelQuery pageQuery, HttpServletRequest request) {
        List<DataSourceModel> data = dataSourceModelService.selectAllList(pageQuery);
        String username = UserUtils.getLoginUser(request);
        data.forEach(element -> {
            //Bind authority scopes
            if (StringUtils.isNotBlank(element.getCreateUser()) && !element.getCreateUser().equals(username)) {
                //Remove sensitive data
                Map<String, Object> params = element.resolveParams();
                Map<String, Object> newParams = new HashMap<>();
                newParams.put(Constants.PARAM_AUTH_TYPE, params.getOrDefault(Constants.PARAM_AUTH_TYPE, AuthType.NONE));
                element.setParameter(Json.toJson(newParams, null));
            }
        });
        Message message = Message.ok();
        message.data("list", data);
        if (!data.isEmpty()) {
            message.data("total", data.size());
        }
        return message;
    }

    @RequestMapping(value = "/{dsType}/keyDefines", method = RequestMethod.GET)
    public Message keyDefines(@PathVariable String dsType, HttpServletRequest request) {
        DataSourceModelTypeKeyQuery pageQuery = new DataSourceModelTypeKeyQuery();
        pageQuery.setDsType(dsType);
        try {
            List<DataSourceModelTypeKey> keyDefines =
                    dataSourceModelTypeKeyService.queryDsModelTypeKeys(UserUtils.getLoginUser(request), pageQuery);
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

    private boolean isDuplicate(String tsName) {
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelExactName(tsName);
        return !dataSourceModelService.selectAllList(query).isEmpty();
    }
}
