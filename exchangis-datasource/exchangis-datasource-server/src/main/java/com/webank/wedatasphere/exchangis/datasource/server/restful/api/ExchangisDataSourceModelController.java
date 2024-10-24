package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.enums.AuthType;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.domain.*;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.exception.DataSourceModelOperateException;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelTypeKeyService;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
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
    public Message add(@Valid @RequestBody DataSourceModel model, HttpServletRequest request) {
        if (StringUtils.isNotBlank(model.getCreateUser()) || isDuplicate(model.getModelName())) {
            return Message.error("The name of model already exists(模板名已存在)");
        }
        String userName = UserUtils.getLoginUser(request);
        model.setCreateUser(userName);
        if (GlobalConfiguration.isAdminUser(userName)) {
            model.setCreateOwner("");
        } else {
            model.setCreateOwner(userName);
        }
        model.setCreateUser(userName);
        if (StringUtils.isBlank(model.getModifyUser())) {
            model.setModifyUser(model.getCreateUser());
        }
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));
        boolean ok = dataSourceModelService.add(model);
        return ok ? Message.ok() :
                Message.error("Failed ti add datasource model");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Message update(@PathVariable Long id, @Valid @RequestBody DataSourceModel model, HttpServletRequest request) {
        String username = UserUtils.getLoginUser(request);
        if (id <= 0) {
            Message.error("Error dataSource model");
        }
        model.setId(id);
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));
        String operator = UserUtils.getLoginUser(request);
        if (StringUtils.isBlank(model.getModifyUser())) {
            model.setModifyUser(operator);
        }
        try {
            // Check if the parameter is updated?
            DataSourceModel before = this.dataSourceModelService.get(id);
            if (Objects.isNull(before)) {
                Message.error("The model is not exist, please check it");
            }
            if (!GlobalConfiguration.isAdminUser(username) &&
                !StringUtils.equals(username, before.getCreateUser())) {
                String msg = String.format("User %s has no permission to operate it!", username);
                return Message.error(msg);
            }
            if (Objects.equals(before.getParameter(), model.getParameter())){
                dataSourceModelService.update(model);
            } else {
                dataSourceModelService.updateRelated(model);
            }
        } catch (DataSourceModelOperateException e) {
            return Message.error("Failed to update the dataSource model, cause by : " + e.getMessage());
        } catch (ExchangisDataSourceException | ExchangisOnEventException e) {
            return Message.error("Failed to update the related dataSource, cause by : " + e.getMessage());
        }
        return Message.ok();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(@PathVariable Long id, HttpServletRequest request) {
        String username = UserUtils.getLoginUser(request);
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelId(id);
        boolean result = false;
        try {
            DataSourceModel before = this.dataSourceModelService.get(id);
            if (Objects.isNull(before)) {
                Message.error("The model is not exist, please check it");
            }
            if (!GlobalConfiguration.isAdminUser(username) &&
                    !StringUtils.equals(username, before.getCreateUser())) {
                String msg = String.format("User %s has no permission to operate it!", username);
                return Message.error(msg);
            }
            result = dataSourceModelService.delete(id);
        } catch (DataSourceModelOperateException | RateLimitOperationException e) {
            return Message.error("Failed to delete the dataSource model, cause by : " + e.getMessage());
        }
        return result ? Message.ok() :
                Message.error("Failed to delete the dataSource model");
    }

    /**
     * Get datasource model detail
     */
    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getDsModelDetail(HttpServletRequest request, @PathVariable("id") Long id) {
        DataSourceModel dataSourceModel = dataSourceModelService.get(id);
        if (Objects.isNull(dataSourceModel)) {
            return Message.error("Not found the dataSource model (找不到对应集群模板)");
        }
        return Message.ok().data("info", dataSourceModel);
    }

    @RequestMapping(value = "/{modelType:\\w+}/list", method = RequestMethod.GET)
    public Message listByType(@PathVariable("modelType") String modelType,
                              HttpServletRequest request) {
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setSourceType(modelType);
        String username = UserUtils.getLoginUser(request);
        if (! GlobalConfiguration.isAdminUser(username)) {
            query.setCreateOwner(username);
        }
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
        PageResult<DataSourceModel> pageResult = null;
        int pageSize = pageQuery.getPageSize();
        if (pageSize == 0) {
            pageQuery.setPageSize(10);
        }
        String username = UserUtils.getLoginUser(request);
        if (!GlobalConfiguration.isAdminUser(username)) {
            pageQuery.setCreateOwner(username);
        }
        pageResult = dataSourceModelService.findPage(pageQuery);
        pageResult.getList().forEach(element -> {
            //Bind authority scopes
            if (StringUtils.isNotBlank(element.getCreateUser()) && !element.getCreateUser().equals(username)) {
                //Remove sensitive data
                Map<String, Object> params = element.resolveParams();
                Map<String, Object> newParams = new HashMap<>();
                newParams.put(Constants.PARAM_AUTH_TYPE, params.getOrDefault(Constants.PARAM_AUTH_TYPE, AuthType.NONE));
                element.setParameter(Json.toJson(newParams, null));
            }
        });
        return Message.ok().data("list", pageResult.getList()).data("total", pageResult.getTotal());
    }

    @RequestMapping(value = "selectAll", method = RequestMethod.GET)
    public Message selectAll(DataSourceModelQuery pageQuery, HttpServletRequest request) {
        String username = UserUtils.getLoginUser(request);
        if (!GlobalConfiguration.isAdminUser(username)) {
            pageQuery.setCreateOwner(username);
        }
        List<DataSourceModel> data = dataSourceModelService.selectAllList(pageQuery);
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
            List<Map<String, Object>> keyDefines =
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
