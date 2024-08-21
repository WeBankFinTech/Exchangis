package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "dss/exchangis/main/datasources", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceRestfulApi.class);


    private final ExchangisDataSourceService exchangisDataSourceService;

    private static final Pattern ERROR_PATTERN = Pattern.compile("(?<=\\[)[^]]+");

    @Resource
    private ProjectOpenService projectOpenService;

    @Autowired
    public ExchangisDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // list all datasource types
    @RequestMapping( value = "/type", method = RequestMethod.GET)
    public Message listDataSourceTypes(HttpServletRequest request,
                                       @RequestParam(value = "engineType", required = false) String engineType,
                                       @RequestParam(value = "direct", required = false) String direct,
                                       @RequestParam(value = "sourceType", required = false) String sourceType) throws Exception {
        Message message = null;
        LOG.info("engineType:{}, direct:{}, sourceType:{}", engineType, direct, sourceType);
        try{
            message = exchangisDataSourceService.listDataSources(request, engineType, direct, sourceType);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while list datasource type";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource type list fail (获取数据源类型列表失败)");
            }
        }
        return message;

    }

    // query paged datasource
    @RequestMapping( value = "/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Message query(HttpServletRequest request, @RequestBody DataSourceQueryVo vo) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSources(request, vo);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("查询数据源失败");
        }
        return message;

    }

    // list all datasources
    @RequestMapping( value = "", method = RequestMethod.GET)
    @Deprecated
    public Message listAllDataSources(
            HttpServletRequest request,
            @RequestParam(value = "typeId", required = false) Long typeId,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.listAllDataSources(request, typeName, typeId, page, size);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource list";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource list fail (获取数据源列表失败)");
            }
        }
        return message;

    }

    // get datasource key define
    @RequestMapping( value = "/types/{dataSourceTypeId}/keydefines", method = RequestMethod.GET)
    public Message getDataSourceKeyDefine(
            HttpServletRequest request,
            @PathVariable("dataSourceTypeId") Long dataSourceTypeId
    ) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceKeyDefine(request, dataSourceTypeId);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource key define";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源主键定义失败");
        }
        return message;

    }


    // get datasource version list
    @RequestMapping( value = "/{id}/versions", method = RequestMethod.GET)
    public Message getDataSourceVersionsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceVersionsById(request, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource version";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource version fail (获取数据源版本失败)");
            }
        }
        return message;

    }

    // create datasource
    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message create(/*@PathParam("type") String type, */
            @Valid @RequestBody DataSourceCreateVo dataSourceCreateVo,
            BindingResult bindingResult, HttpServletRequest request ) throws Exception {
        Message result;
        String loginUser = UserUtils.getLoginUser(request);
        String originUser = SecurityFilter.getLoginUsername(request);
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.DATASOURCE,"0", "DataSource name is: " + dataSourceCreateVo.getDataSourceName(),
                OperateTypeEnum.CREATE,request);
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                return Message.error("[Error](" + fieldError.getField() + "):" + fieldError.getDefaultMessage());
            }
        }
        try {
            String comment = dataSourceCreateVo.getComment();
            String createSystem = dataSourceCreateVo.getCreateSystem();
            if (Objects.isNull(comment)) {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(),
                        "parameter comment should not be null");
            }
            if (Strings.isNullOrEmpty(createSystem)) {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(),
                        "parameter createSystem should not be empty");
            }
            Map<String, Object> versionData = exchangisDataSourceService.create(loginUser, dataSourceCreateVo);
            result = Message.ok();
            versionData.forEach(result::data);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while create datasource";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                result = Message.error(matcher.group());
            }
            else{
                result = Message.error("创建数据源失败:[" + errorNote + "]");
            }
        }
        return result;
    }

    // get datasource details
    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getDataSourceInfoById(HttpServletRequest request, @PathVariable("id") Long id, @QueryParam(value = "versionId") String versionId) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSource(request, id, versionId);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource information";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource information fail (获取数据源信息失败)");
            }
        }
        return message;

    }

    @RequestMapping( value = "/{id}/connect_params", method = RequestMethod.GET)
    public Message getDataSourceConnectParamsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceConnectParamsById(request, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting connect params";
            LOG.error(errorMessage, e);
            message = Message.error("Exit same name dataSource(获取数据源连接参数失败)");
        }
        return message;

    }

    /**
     * update datasource and parameters (insert new record in datasource_version table)
      */
    @RequestMapping( value = "/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest request,
                          @PathVariable("id") Long id,
                          @Valid @RequestBody DataSourceCreateVo updateVo, BindingResult bindingResult) throws Exception {
        Message result;
        String originUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource name is: " + updateVo.getDataSourceName(),
                OperateTypeEnum.UPDATE,request);
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                return Message.error("[Error](" + fieldError.getField() + "):" + fieldError.getDefaultMessage());
            }
        }
        try{
            String createSystem = updateVo.getCreateSystem();
            if (Strings.isNullOrEmpty(createSystem)) {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "parameter createSystem should not be empty");
            }
            exchangisDataSourceService.update(loginUser, id, updateVo);
            result = Message.ok();
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while update datasource";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                result = Message.error(matcher.group());
            }
            else{
                result = Message.error("Exit same name dataSource(更新数据源失败):[" + e.getMessage() + "]");
            }
        }
        return result;

    }

    // publish datasource
    @RequestMapping( value = "/{id}/{version}/publish", method = RequestMethod.PUT)
    public Message publishDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                                     @PathVariable("version") Long version) throws Exception {
        Message message = null;
        String loginUser = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        try{
            String username = UserUtils.getLoginUser(request);
            message = exchangisDataSourceService.publishDataSource(username, /*type, */id, version);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while publish datasource";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Publish datasource failed (发布数据源失败)");
            }
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource publish", OperateTypeEnum.PUBLISH, request);
        return message;

    }

    // expire datasource
    @RequestMapping( value = "/{id}/expire", method = RequestMethod.PUT)
    public Message expireDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        Message message = null;
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        try{
            message = exchangisDataSourceService.expireDataSource(request, /*type, */id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while expire datasource";
            LOG.error(errorMessage, e);
            message = Message.error("过期数据源失败");
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource expire", OperateTypeEnum.PUBLISH, request);
        return message;

    }

    // test datasource connect
    @RequestMapping( value = "/{id}/{version}/connect", method = RequestMethod.PUT)
    public Message testConnect(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                               @PathVariable("version") Long version) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.testConnect(request, /*type, */id, version);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Connect datasource failed (数据源连接失效，请检查配置)");
            }
        }
        return message;
    }

    @RequestMapping( value = "/op/connect", method = RequestMethod.POST)
    public Message testConnectByMap(HttpServletRequest request,/* @PathParam("type") String type, */@Valid @RequestBody DataSourceCreateVo dataSourceCreateVO,
                                    BindingResult bindingResult) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.testConnectByVo(request, /*type, */dataSourceCreateVO);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Connect datasource failed (数据源连接失效，请检查配置)");
            }
        }
        return message;
    }

    // delete datasource (physical)
    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest request, /*@PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        Message message = null;
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        try{
            message = exchangisDataSourceService.deleteDataSource(request, /*type, */id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while delete datasource";
            LOG.error(errorMessage, e);
            message = Message.error("删除数据源失败，存在引用依赖");
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource delete", OperateTypeEnum.DELETE, request);
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs", method = RequestMethod.GET)
    public Message queryDataSourceDBs(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            message = exchangisDataSourceService.queryDataSourceDBs(username.get(), type, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("数据源未发布或参数为空");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables", method = RequestMethod.GET)
    public Message queryDataSourceDBTables(HttpServletRequest request, @PathVariable("type") String type,
                                           @PathVariable("id") Long id, @PathVariable("dbName") String dbName) throws Exception {
        Message message = null;
        try {
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            message = exchangisDataSourceService.queryDataSourceDBTables(username.get(), type, id, dbName);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting tables";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据表失败");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields", method = RequestMethod.GET)
    public Message queryDataSourceDBTableFields(HttpServletRequest request, @PathVariable("type") String type,
                                                @PathVariable("id") Long id, @PathVariable("dbName") String dbName,
                                                @PathVariable("tableName") String tableName) throws Exception {

        Message message = null;
        try{
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            message = exchangisDataSourceService.queryDataSourceDBTableFields(username.get(), type, id, dbName, tableName);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting table fields";
            LOG.error(errorMessage, e);
            message = Message.error("获取表字段失败");
        }
        return message;

    }

    @RequestMapping( value = "/tools/encrypt", method = RequestMethod.POST)
    public Message sourceStrEncrypt(HttpServletRequest request, @RequestBody Map<String, Object> params, @QueryParam(value = "encryStr") String encryStr) throws Exception {
        Message message = null;
        try{
            LOG.info("Encrypt params is: {}", params);
            message = exchangisDataSourceService.encryptConnectInfo((String) params.get("encryStr"));
            //message = Message.ok().data("encryStr", "owwonowoww");
        } catch (Exception e) {
            String errorMessage = "Encrypted string failed";
            LOG.error(errorMessage, e);
            message = Message.error("加密字符串失败");
        }
        return message;
    }

    @RequestMapping( value = "/tools/decrypt", method = RequestMethod.POST)
    public Message sinkStrDecrypt(HttpServletRequest request, @RequestBody Map<String, Object> params, @QueryParam(value = "sinkStr") String sinkStr) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.decryptConnectInfo((String) params.get("sinkStr"));
            //message = Message.ok().data("encryStr", "owwonowoww");
        } catch (Exception e) {
            String errorMessage = "Encrypted string failed";
            LOG.error(errorMessage, e);
            message = Message.error("加密字符串失败");
        }
        return message;
    }

    @RequestMapping( value = "/{engine}/{type}/params/ui", method = RequestMethod.GET)
    public Message getParamsUI(
            HttpServletRequest request,
            @PathVariable("engine") String engine,
            @PathVariable("type") String type,
            @RequestParam(value = "dir", required = false) String dir
    ) {
        List<ElementUI<?>> uis = this.exchangisDataSourceService.getDataSourceParamsUI(type, String.format("%s-%s", engine, dir));
        return Message.ok().data("uis", uis);
    }

}
