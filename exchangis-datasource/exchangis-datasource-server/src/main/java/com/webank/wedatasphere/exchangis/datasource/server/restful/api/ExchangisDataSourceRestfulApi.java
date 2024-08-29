package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceItem;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceTypeDefinition;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceUIGetter;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.datasource.utils.RSAUtil;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import org.apache.commons.lang.StringUtils;
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
import java.security.PrivateKey;
import java.security.PublicKey;
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

    private final DataSourceService dataSourceService;

    private static final Pattern ERROR_PATTERN = Pattern.compile("(?<=\\[)[^]]+");

    @Resource
    private ProjectOpenService projectOpenService;

    @Resource
    private DataSourceUIGetter uiGetter;

    @Autowired
    public ExchangisDataSourceRestfulApi(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    /**
     * List all datasource types
     */
    @RequestMapping( value = "/type", method = RequestMethod.GET)
    public Message listDataSourceTypes(HttpServletRequest request,
                                       @RequestParam(value = "engineType", required = false) String engineType,
                                       @RequestParam(value = "direct", required = false) String direct,
                                       @RequestParam(value = "sourceType", required = false) String sourceType) throws Exception {
        Message message = null;
        LOG.info("engineType:{}, direct:{}, sourceType:{}", engineType, direct, sourceType);
        try{
            List<ExchangisDataSourceTypeDefinition> typeDefinitions
                    = dataSourceService.listDataSourceTypes(UserUtils.getLoginUser(request), engineType, direct, sourceType);
            message = Message.ok().data("list", typeDefinitions);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while list datasource type";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource type list fail (获取数据源类型列表失败):[" + e.getMessage() + "]");
            }
        }
        return message;

    }

    /**
     * Query paged datasource
     */
    @RequestMapping( value = "/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Message query(HttpServletRequest request, @RequestBody DataSourceQueryVo vo) throws Exception {
        Message message;
        try{
            PageResult<ExchangisDataSourceItem> result =
                    dataSourceService.queryDataSources(UserUtils.getLoginUser(request), vo);
            return Message.ok().data("total", result.getTotal()).data("list", result.getList());
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("查询数据源失败:[" + e.getMessage() + "]");
        }
        return message;

    }

    /**
     * List all datasources
      */
    @RequestMapping( value = "", method = RequestMethod.GET)
    @Deprecated
    public Message listDataSources(
            HttpServletRequest request,
            @RequestParam(value = "typeId", required = false) Long typeId,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        Message message = null;
        try{
            message = Message.ok().data("list",
                    dataSourceService.listDataSources(UserUtils.getLoginUser(request),
                    typeName, typeId, page, size));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource list";
            LOG.error(errorMessage, e);

            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource list fail (获取数据源列表失败):[" + e.getMessage() + "]");
            }
        }
        return message;

    }

    /**
     * Get datasource key define
     */
    @RequestMapping( value = "/types/{dataSourceTypeId}/keydefines", method = RequestMethod.GET)
    public Message getDataSourceKeyDefine(
            HttpServletRequest request,
            @PathVariable("dataSourceTypeId") Long dataSourceTypeId
    ) {
        Message message;
        try{
            message = Message.ok().data("list", dataSourceService
                    .getDataSourceKeyDefine(UserUtils.getLoginUser(request), dataSourceTypeId));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource key define";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源主键定义失败:[" + e.getMessage() + "]");
        }
        return message;

    }


    /**
     * Get datasource version list
     */
    @RequestMapping( value = "/{id}/versions", method = RequestMethod.GET)
    public Message getDataSourceVersionsById(HttpServletRequest request, @PathVariable("id") Long id) {
        Message message;
        try{
            message = Message.ok().data("versions", dataSourceService
                    .getDataSourceVersionsById(UserUtils.getLoginUser(request), id));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource version";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource version fail (获取数据源版本失败):[" + e.getMessage() + "]");
            }
        }
        return message;

    }

    // create datasource
    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message create(/*@PathParam("type") String type, */
            @Valid @RequestBody DataSourceCreateVo dataSourceCreateVo,
            BindingResult bindingResult, HttpServletRequest request ) {
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
            Map<String, Object> versionData = dataSourceService.create(loginUser, dataSourceCreateVo);
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

    /**
     * Get datasource details
     */
    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getDataSourceInfoById(HttpServletRequest request,
                                         @PathVariable("id") Long id, @QueryParam(value = "versionId") String versionId) {
        Message message;
        try{
            message = Message.ok().data("info", dataSourceService
                    .getDataSource(UserUtils.getLoginUser(request), id, versionId));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource information";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Getting datasource information fail (获取数据源信息失败):[" + e.getMessage() + "]");
            }
        }
        return message;
    }

    /**
     * Get data source connect params
     * @param request request
     * @param id data source id
     * @return message
     */
    @RequestMapping( value = "/{id}/connect_params", method = RequestMethod.GET)
    public Message getDataSourceConnectParamsById(HttpServletRequest request, @PathVariable("id") Long id) {
        Message message;
        try{
            message = Message.ok().data("info", dataSourceService
                    .getDataSourceConnectParamsById(UserUtils.getLoginUser(request), id));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting connect params";
            LOG.error(errorMessage, e);
            message = Message.error("Exit same name dataSource(获取数据源连接参数失败):[" + e.getMessage() + "]");
        }
        return message;

    }

    /**
     * update datasource and parameters (insert new record in datasource_version table)
     */
    @RequestMapping( value = "/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest request,
                          @PathVariable("id") Long id,
                          @Valid @RequestBody DataSourceCreateVo updateVo, BindingResult bindingResult) {
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
            dataSourceService.update(loginUser, id, updateVo);
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

    /**
     * Publish data source
     */
    @RequestMapping( value = "/{id}/{version}/publish", method = RequestMethod.PUT)
    public Message publishDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                                     @PathVariable("version") Long version) {
        Message message;
        String loginUser = UserUtils.getLoginUser(request);
        String originUser = SecurityFilter.getLoginUsername(request);
        try{
            String username = UserUtils.getLoginUser(request);
            dataSourceService.publishDataSource(username, id, version);
            message = Message.ok();
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while publish datasource";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Publish datasource failed (发布数据源失败):[" + e.getMessage() + "]");
            }
        }
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource publish", OperateTypeEnum.PUBLISH, request);
        return message;

    }

    /**
     * Expire data source
     */
    @RequestMapping( value = "/{id}/expire", method = RequestMethod.PUT)
    public Message expireDataSource(HttpServletRequest request, @PathVariable("id") Long id) {
        Message message;
        String originUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        try{
            dataSourceService.expireDataSource(UserUtils.getLoginUser(request), id);
            message = Message.ok();
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while expire datasource";
            LOG.error(errorMessage, e);
            message = Message.error("过期数据源失败:[" + e.getMessage() + "]");
        }
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource expire", OperateTypeEnum.PUBLISH, request);
        return message;

    }

    /**
     * Test data source connect
     */
    @RequestMapping( value = "/{id}/{version}/connect", method = RequestMethod.PUT)
    public Message testConnect(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                               @PathVariable("version") Long version) {
        Message message;
        try{
            dataSourceService.testConnect(UserUtils.getLoginUser(request),
                    id, version);
            message = Message.ok();
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Connect datasource failed (数据源连接失效，请检查配置):[" + e.getMessage() + "]");
            }
        }
        return message;
    }

    /**
     * Test data source connect by map
     * @param request request
     * @param dataSourceCreateVO vo
     * @param bindingResult binding
     * @return message
     */
    @RequestMapping( value = "/op/connect", method = RequestMethod.POST)
    public Message testConnectByMap(HttpServletRequest request, @Valid @RequestBody DataSourceCreateVo dataSourceCreateVO,
                                    BindingResult bindingResult) {
        Message message = null;
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                return Message.error("[Error](" + fieldError.getField() + "):" + fieldError.getDefaultMessage());
            }
        }
        try{
            dataSourceService.testConnectByVo(UserUtils.getLoginUser(request),
                    dataSourceCreateVO);
            message = Message.ok();
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);
            String errorNote = e.getMessage();
            Matcher matcher = ERROR_PATTERN.matcher(errorNote);
            if (matcher.find()) {
                message = Message.error(matcher.group());
            }
            else{
                message = Message.error("Connect datasource failed (数据源连接失效，请检查配置):[" + e.getMessage() + "]");
            }
        }
        return message;
    }

    /**
     * Delete data source
     * @param request request
     * @param id id
     * @return message
     */
    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest request,
                          @PathVariable("id") Long id) {
        Message message;
        String originUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        try{
            message = Message.ok().data("id",
                    dataSourceService.delete(loginUser, id));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while delete datasource";
            LOG.error(errorMessage, e);
            message = Message.error("删除数据源失败:[" + e.getMessage() + "]");
        }
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.DATASOURCE, id.toString(), "DataSource delete", OperateTypeEnum.DELETE, request);
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs", method = RequestMethod.GET)
    public Message getDatabases(HttpServletRequest request,
                                @PathVariable("type") String type, @PathVariable("id") Long id) {
        Message message;
        try{
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            List<String> databases = dataSourceService.getDatabases(username.get(), type, id);
            message = Message.ok().data("dbs", databases);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("数据源未发布或参数为空:[" + e.getMessage() + "]");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables", method = RequestMethod.GET)
    public Message getTables(HttpServletRequest request, @PathVariable("type") String type,
                                           @PathVariable("id") Long id, @PathVariable("dbName") String dbName) throws Exception {
        Message message;
        try {
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            message = Message.ok().data("tbs",
                    dataSourceService.getTables(username.get(), type, id, dbName));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting tables";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据表失败:[" + e.getMessage() + "]");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields", method = RequestMethod.GET)
    public Message getTableFields(HttpServletRequest request, @PathVariable("type") String type,
                                                @PathVariable("id") Long id, @PathVariable("dbName") String dbName,
                                                @PathVariable("tableName") String tableName) throws Exception {

        Message message;
        try{
            AtomicReference<String> username = new AtomicReference<>(UserUtils.getLoginUser(request));
            // Try to get data source authority from project and set the privilege user
            projectOpenService.hasDataSourceAuth(username.get(), id,
                    ds -> username.set(ds.getCreator()));
            message = Message.ok().data("columns",
                    dataSourceService.getTableFields(username.get(), type, id, dbName, tableName));
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting table fields";
            LOG.error(errorMessage, e);
            message = Message.error("获取表字段失败:[" + e.getMessage() + "]");
        }
        return message;

    }

    @RequestMapping( value = "/tools/encrypt", method = RequestMethod.POST)
    public Message sourceStrEncrypt(HttpServletRequest request, @RequestBody Map<String, Object> params, @QueryParam(value = "encryStr") String encryStr) throws Exception {
        Message message = null;
        try{
            String encryptStr = (String) params.get("encryStr");
            if (StringUtils.isBlank(encryptStr)) {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType connect parameter show not be null");
            }
            String publicKeyStr = RSAUtil.PUBLIC_KEY_STR.getValue();
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
            byte[] publicEncrypt = RSAUtil.publicEncrypt(encryptStr.getBytes(), publicKey);
            message = Message.ok().data("encryStr", RSAUtil.byte2Base64(publicEncrypt));
        } catch (Exception e) {
            String errorMessage = "Encrypted string failed";
            LOG.error(errorMessage, e);
            message = Message.error("加密字符串失败:[" + e.getMessage() + "]");
        }
        return message;
    }

    @RequestMapping( value = "/tools/decrypt", method = RequestMethod.POST)
    public Message sinkStrDecrypt(HttpServletRequest request, @RequestBody Map<String, Object> params, @QueryParam(value = "sinkStr") String sinkStr) throws Exception {
        Message message = null;
        try{
            sinkStr = (String) params.get("sinkStr");
            if (Objects.isNull(sinkStr)) {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType connect parameter show not be null");
            }
            String privateKeyStr = RSAUtil.PRIVATE_KEY_STR.getValue();
            PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
            byte[] base642Byte = RSAUtil.base642Byte(sinkStr);
            byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
            String decryptStr = new String(privateDecrypt);
            message = Message.ok().data("decryptStr", decryptStr);
        } catch (Exception e) {
            String errorMessage = "Encrypted string failed";
            LOG.error(errorMessage, e);
            message = Message.error("加密字符串失败:[" + e.getMessage() + "]");
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
        List<ElementUI<?>> uis = this.uiGetter.getDataSourceParamsUI(type, String.format("%s-%s", engine, dir));
        return Message.ok().data("uis", uis);
    }

}
