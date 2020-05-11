/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.datasource.controller;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.checks.AbstractDataSourceConnCheck;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.exception.ConnParamsInValidException;
import com.webank.wedatasphere.exchangis.datasource.query.DataSourceQuery;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceModelServiceImpl;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.job.service.JobInfoService;
import com.webank.wedatasphere.exchangis.project.domain.Project;
import com.webank.wedatasphere.exchangis.project.service.ProjectService;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Data source management
 * @author Created by devendeng on 2018/8/23.
 */
@RestController
@RequestMapping("/api/v1/datasource")
public class DataSourceController extends AbstractGenericController<DataSource, DataSourceQuery> {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceController.class);
    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private JobInfoService jobInfoService;

    @Resource
    private Configuration conf;

    @Resource
    private GroupService groupService;

    @Resource
    private DataSourceModelServiceImpl modelService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ProjectService projectService;
    @Override
    public IBaseService<DataSource> getBaseService() {
        return dataSourceService;
    }

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(DataSource.class, userName -> {
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //Not limit
                return null;
            }
            return groupService.queryGroupRefProjectsByUser(userName);
        });
        security.registerExternalDataAuthGetter(DataSource.class, dataSource -> {
            if (null == dataSource){
                return Collections.emptyList();
            }
            return Collections.singletonList(String.valueOf(dataSource.getProjectId()));
        });
        security.registerExternalDataAuthScopeGetter(DataSource.class, dataSource -> {
            if(null == dataSource || dataSource.getId() <= 0){
                return new ArrayList<>();
            }
            List<DataAuthScope> permissions = dataSourceService.getPermission(dataSource.getId());
            permissions.add(DataAuthScope.READ);
            return permissions;
        });
    }

    /**
     * Exception for connection parameters
     * @param e
     * @return
     */
    @ExceptionHandler(value = ConnParamsInValidException.class)
    public Response<Object> errorParameterHandler(ConnParamsInValidException e){
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, e.getMessage());
    }

    /**
     * Connection test
     * @param type data source type
     * @param authFile authentication file
     * @param dataSource data source entity
     * @param result binding result
     * @param resp response
     * @return
     */
    @RequestMapping(value = "/connect/check/{type:\\w+}", method = RequestMethod.POST)
    public Response<Object> connectionCheck(@PathVariable("type") String type,
                                             @RequestParam(value = "authFile", required = false) MultipartFile authFile,
                                            @Validated DataSource dataSource, BindingResult result,
                                            HttpServletResponse resp) {
        if (result.hasErrors()) {
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    result.getFieldErrors().get(0).getDefaultMessage());
        }
        DataSourceConnCheck connCheck = AbstractDataSourceConnCheck.getConnCheck(type, resp);
        if (null == connCheck) {
            return new Response<>().errorResponse(CodeConstant.DATASOURCE_CHECK_NOT_EXISTS, null, super.informationSwitch("exchange.data_source.check.not.exists"));
        }
        dataSource.setSourceType(type);
        if(dataSource.getId() > 0){
           DataSource dataSource1 = dataSourceService.getDecryptedSimpleOne(dataSource.getId());
           if(StringUtils.isBlank(dataSource.getAuthCreden()) && StringUtils.isNotBlank(dataSource.getAuthEntity())){
               dataSource.setAuthCreden(dataSource1.getAuthCreden());
           }
        }
        if(null != dataSource.getModelId()) {
            dataSourceService.fillDataSourceWithModel(dataSource, dataSource.getModelId());
        }
        File authFileTmp = null;
        try {
            if (null != authFile && !authFile.isEmpty() && null != authFile.getOriginalFilename()) {
                authFileTmp = new File(conf.getStoreTmp() + AppUtil.newFileName(authFile.getOriginalFilename()));
                if(new File(conf.getStoreTmp()).mkdirs()){
                    LOG.info("Created temporary dir: " + conf.getStoreTmp());
                }
                if (authFileTmp.createNewFile()) {
                    FileUtils.copyInputStreamToFile(authFile.getInputStream(), authFileTmp);
                }
            }
        }catch (IOException e) {
            LOG.error("IO_ERROR: Transforming 'authFile' input stream to temp file error, message: " + e.getMessage(), e);
            throw new EndPointException(super.informationSwitch("exchange.data_source.systematic.io.anomaly"),  e);
        }
        try {
            boolean ok = true;
            String alarm = "";
            String message = "";
            try {
                LOG.info(Json.toJson(dataSource, null));
                connCheck.check(dataSource, authFileTmp);
            } catch (Exception e) {
                LOG.info("Connection check failed: " + e.getMessage());
                LOG.error(e.getMessage(), e);
                message = e.getMessage();
                if(StringUtils.isNotBlank(message)){
                    alarm = super.informationSwitch("exchange.data_source.connection.error");
                }
                ok = false;
            }
            return ok ? new Response<>().successResponse(null) :
                    new Response<>().errorResponse(CodeConstant.DATASOURCE_CONN_ERROR, null, alarm, message);
        } finally {
            if (null != authFileTmp) {
                if(!authFileTmp.delete()){
                   LOG.error("IO_ERROR: delete temp file Failed, Path: [" + authFileTmp.getPath()+"]");
                }
            }
        }
    }


    /**
     * Download authentication file
     * @param request request
     * @param response response
     * @param authPrefix prefix path
     * @throws IOException
     */
    @RequestMapping(value = "/{auth:kb|key}/**", method = RequestMethod.GET)
    public void authFileDownload(HttpServletRequest request,
                           HttpServletResponse response, @PathVariable("auth")String authPrefix) throws IOException {
        String path = getPathFromReq(request, authPrefix);
        if(null == path){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        FileSystemResource fsResource = new FileSystemResource(path);
        if (fsResource.exists() && fsResource.isReadable()) {
            String name = "auth";
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(fsResource.contentLength());
            response.setHeader("Content-Disposition",
                    String.format("attachment;filename=\"%s\"", name));
            try (InputStream inputStream = fsResource.getInputStream()) {
                IOUtils.copy(inputStream, response.getOutputStream());
            }
            String fileName = fsResource.getFilename();
            if(StringUtils.isNotBlank(fileName) && fileName.startsWith(DataSourceServiceImpl.PERSIST_DISPOSABLE_PREFIX)){
                if(!fsResource.getFile().delete()){
                    LOG.error("IO_ERROR: delete disposable auth file Failed, Path:[" + fsResource.getPath()+"]");
                }
            }
        } else {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(Objects.requireNonNull(Json.toJson(new Response<>().errorResponse(CodeConstant.FILE_NOT_FOUND, null,
                    super.informationSwitch("exchange.data_source.invalid.file.path")), Response.class)));
        }
        response.flushBuffer();

    }

    /**
     * Delete authentication file
     * @param request request
     * @param response response
     * @param authPrefix prefix path
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/{auth:kb|key}/**", method = RequestMethod.DELETE)
    public Response<Boolean> authFileRemove(HttpServletRequest request,
                                                       HttpServletResponse response,
                                      @PathVariable("auth")String authPrefix) throws IOException{
        String path = getPathFromReq(request, authPrefix);
        FileSystemResource fsResource = new FileSystemResource(path);
        if(fsResource.exists()){
            if(fsResource.getFile().delete()){
                return new Response<Boolean>().successResponse(true);
            }
        }
        return new Response<Boolean>().errorResponse(CodeConstant.FILE_NOT_FOUND, false, super.informationSwitch("exchange.data_source.file.notFind"));
    }
    @Override
    public Response<DataSource> add(@Valid @RequestBody DataSource t, HttpServletRequest request) {
        return add(null, t, null, request, null);
    }

    /**
     * Add with attach file
     * @param authFile file
     * @param dataSource data source
     * @param result binding result
     * @param request request
     * @param resp response
     * @return
     */
    @RequestMapping(value = "/addAttach", method = RequestMethod.POST)
    public Response<DataSource> add(
            @RequestParam(value = "authFile", required = false) MultipartFile authFile,
            @Validated DataSource dataSource, BindingResult result, HttpServletRequest request, HttpServletResponse resp) {
        if (null != result && result.hasErrors()) {
            return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    result.getFieldErrors().get(0).getDefaultMessage());
        }

        File storeFile = null;
        String path = null;
        DataSourceConnCheck connCheck = AbstractDataSourceConnCheck.getConnCheck(dataSource.getSourceType(), resp);
        if (null == connCheck) {
            return null;
        }
        security.bindUserInfo(dataSource, request);
        if(StringUtils.isNotBlank(dataSource.getCreateUser())
                && isDuplicate(dataSource.getSourceName(), dataSource.getCreateUser())){
            return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source.with.same.name"));
        }
        if( null != dataSource.getModelId() ){
            if(!modelService.exist(dataSource.getModelId())) {
                return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("udes.datasource.invalid.datasource.id"));
            }
            dataSourceService.fillDataSourceWithModel(dataSource, dataSource.getModelId());
        }
        try {
            if (null != authFile && !authFile.isEmpty()) {
                String authType = String.valueOf(dataSource.getParameterMap().get(Constants.PARAM_AUTH_TYPE));
                storeFile = dataSourceService.store(authFile, authType);
                path = AppUtil.getIpAndPort() + conf.getStoreUrl(authType) + storeFile.getName();
            }
            if (null != path) {
                dataSource.setAuthCreden(path);
            }
            dataSource.setSourceName(StringEscapeUtils.escapeHtml3(dataSource.getSourceName()));
            dataSource.setSourceDesc(dataSource.getSourceDesc());
            //Validate if the parameter is json structure
            Json.fromJson(dataSource.getParameter(), Map.class, String.class, Object.class);
            boolean ok = getBaseService().add(dataSource);
            return ok ? new Response<DataSource>().successResponse(null) :
                    new Response<DataSource>().errorResponse(CodeConstant.DATASOURCE_ADD_ERROR, null, super.informationSwitch("exchange.data_source.add.failed"));
        } catch (Exception e) {
            if (null != storeFile) {
                if(!storeFile.delete()){
                    LOG.error("IO_ERROR: delete store file Failed, Path: [" + storeFile.getPath()+"]");
                }
            }
            if(e instanceof  ConnParamsInValidException){
                throw (ConnParamsInValidException)e;
            }
            throw new RuntimeException(e);
        }

    }

    @Override
    public Response<DataSource> update(@Valid @RequestBody DataSource dataSource, HttpServletRequest request) {
        return update(null, dataSource,  null, request, null);
    }

    /**
     * Update with attach file
     * @param authFile file
     * @param dataSource data source
     * @param result binding result
     * @param request request
     * @param resp response
     * @return
     */
    @RequestMapping(value = "/updateAttach", method = RequestMethod.POST)
    public Response<DataSource> update(
            @RequestParam(value = "authFile", required = false) MultipartFile authFile,
            @Validated DataSource dataSource, BindingResult result, HttpServletRequest request, HttpServletResponse resp) {
        if(null != result && result.hasErrors()){
            return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    result.getFieldErrors().get(0).getDefaultMessage());
        }
        if(dataSource.getId()  <= 0){
            return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source.id.not.empty"));
        }
        DataSource dsOld = dataSourceService.getDecryptedSimpleOne(dataSource.getId());
        if(!hasDataAuth(DataSource.class, DataAuthScope.WRITE, request, dsOld)){
            return new Response<DataSource>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        if(!hasDataAuth(DataSource.class, DataAuthScope.ALL, request, dsOld)){
           dataSource.setProjectId(dsOld.getProjectId());
           //Set authScopes to null
           dataSource.setAuthScopes(null);
        }
        File storeFile = null;
        String path = null;
        DataSourceConnCheck connCheck = AbstractDataSourceConnCheck.getConnCheck(dataSource.getSourceType(), resp);
        if( null == connCheck){
            return null;
        }
        security.bindUserInfo(dataSource, request);
        if(!dsOld.getSourceName().equals(dataSource.getSourceName())
                && StringUtils.isNotBlank(dataSource.getCreateUser())
                && isDuplicate(dataSource.getSourceName(), dataSource.getCreateUser())){
            return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source.with.same.name"));
        }
        if( null != dataSource.getModelId() ){
            if(!modelService.exist(dataSource.getModelId())) {
                return new Response<DataSource>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("udes.datasource.invalid.datasource.id"));
            }
            dataSourceService.fillDataSourceWithModel(dataSource, dataSource.getModelId());
        }
        try {
            if (null != authFile && !authFile.isEmpty()) {
                String authType = String.valueOf(dataSource.getParameterMap().get(Constants.PARAM_AUTH_TYPE));
                storeFile = dataSourceService.store(authFile, authType);
                path = AppUtil.getIpAndPort() + conf.getStoreUrl(authType) + storeFile.getName();
            }
            if (null != path) {
                dataSource.setAuthCreden(path);
            }
            dataSource.setSourceName(StringEscapeUtils.escapeHtml3(dataSource.getSourceName()));
            dataSource.setSourceDesc(dataSource.getSourceDesc());
            if(StringUtils.isBlank(dataSource.getAuthCreden()) && StringUtils.isNotBlank(dataSource.getAuthEntity())){
                dataSource.setAuthCreden(dsOld.getAuthCreden());
            }
            Long oldProjectId = dsOld.getProjectId();
            if(null != oldProjectId && oldProjectId > 0 && !oldProjectId.equals(dataSource.getProjectId())){
               if(jobInfoService.isRunWithDataSource(oldProjectId)){
                   Project oldProject = projectService.get(oldProjectId);
                   if(null != oldProject) {
                       return new Response<DataSource>().errorResponse(CodeConstant.DATASOURCE_ADD_ERROR, null,
                               super.informationSwitch("exchange.data_source.project.unbind.not"), oldProject.getProjectName());
                   }
               }
            }
            //Validate if the parameter is json structure
            Json.fromJson(dataSource.getParameter(), Map.class, String.class, Object.class);
            boolean ok = getBaseService().update(dataSource);
            return ok ? new Response<DataSource>().successResponse(null) :
                    new Response<DataSource>().errorResponse(CodeConstant.DATASOURCE_ADD_ERROR, null, super.informationSwitch("exchange.data_source.add.failed"));
        } catch (Exception e) {
            if (null != storeFile) {
                if(!storeFile.delete()){
                    LOG.error("IO_ERROR: delete store file Failed, Path:[" + storeFile.getPath()+"]");
                }
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response<DataSource> delete(@PathVariable Long id, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class,  DataAuthScope.DELETE, request, dataSourceService.get(id))){
            return new Response<DataSource>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        boolean isRunWith = jobInfoService.isRunWithDataSource(id);
        if(isRunWith){
            return new Response<DataSource>().errorResponse(CodeConstant.DATASOURCE_EXIST_JOBS, null, super.informationSwitch("exchange.data_source.related.jobs"));
        }
        return super.delete(id, request);
    }

    @Override
    public Response<DataSource> show(@PathVariable Long id,HttpServletRequest request) throws Exception {
        DataSource dataSource = dataSourceService.get(id);
        if(!hasDataAuth(DataSource.class, DataAuthScope.READ, request, dataSource)){
            return new Response<DataSource>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        return new Response<DataSource>().successResponse(dataSource);
    }

    private String getPathFromReq(HttpServletRequest request, String prefix){
        String uri = request.getRequestURI();
        String path = uri.substring(uri.lastIndexOf("/" + prefix) + 1);
        path = path.replace("../", "/");
        path = path.replace("..", "");
        String resolvePath = conf.getStorePrefix() + IOUtils.DIR_SEPARATOR_UNIX + path;;
        if(Paths.get(resolvePath).normalize()
                .startsWith(Paths.get(conf.getStorePrefix()).normalize())){
            return resolvePath;
        }
        return null;
    }
    /**
     * If the same data source name exists
     * @param dsName
     * @param createUser
     * @return
     */
    private boolean isDuplicate(String dsName, String createUser){
        DataSourceQuery query = new DataSourceQuery();
        query.setCreateUser(createUser);
        query.setSourceName(dsName);
        return !dataSourceService.selectAllList(query).isEmpty();
    }

}
