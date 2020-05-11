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

package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.datasource.dao.DataSourcePermissionDao;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.dao.DataSourceDao;
import com.webank.wedatasphere.exchangis.datasource.dao.DataSourceModelDao;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourcePermission;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.webank.wedatasphere.exchangis.datasource.Constants.PARAM_KERBEROS_BOOLEAN;

/**
 * Created by devendeng on 2018/8/23.
 */
@Service
public class DataSourceServiceImpl extends AbstractGenericService<DataSource> implements DataSourceService {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    private static final String URL_PROTOCOL = "http";

    public static final String PERSIST_DISPOSABLE_PREFIX = "dis_";
    @Resource
    private DataSourceDao dataSourceDao;

    @Resource
    private Configuration conf;

    @Resource
    private DataSourceModelDao modelDao;

    @Resource
    private DataSourcePermissionDao permissionDao;

    @Override
    protected IBaseDao<DataSource> getDao() {
        return dataSourceDao;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public boolean update(DataSource dataSource) {
        DataSource oldDs = dataSourceDao.selectOneAndLock(dataSource.getId());
        Object oldDsAuthCreden = null;
        if(null == dataSource.getAuthCreden()){
            dataSource.setAuthCreden(oldDs.getAuthCreden());
        }else {
            oldDsAuthCreden = oldDs.getAuthCreden();
            if(!isUrl(dataSource.getAuthCreden())){
                dataSource.setAuthCreden(encodeBase64(dataSource.getAuthCreden()));
            }
        }
        List<String> authScopes = dataSource.getAuthScopes();
        if(null != authScopes){
            permissionDao.update(new DataSourcePermission(dataSource.getId(), authScopes));
        }
        boolean result = super.update(dataSource);
        if(result && oldDsAuthCreden != null &&
                isUrl(String.valueOf(oldDsAuthCreden))) {
            try {
                AppUtil.removeFile(String.valueOf(oldDsAuthCreden));
            }catch(Exception e){
                LOG.error("Remove authentication file Error: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(DataSource dataSource) {
        String authCreden = dataSource.getAuthCreden();
        if(StringUtils.isNotBlank(authCreden) && !isUrl(authCreden)){
            dataSource.setAuthCreden(encodeBase64(dataSource.getAuthCreden()));
        }
        List<String> authScopes = dataSource.getAuthScopes();
        boolean result = super.add(dataSource);
        if(result){
            //Add permissions
            DataSourcePermission permission =  null == authScopes ? new DataSourcePermission(dataSource.getId())
                    : new DataSourcePermission(dataSource.getId(), authScopes);
            permissionDao.insertOne(permission);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<Object> ids) {
        boolean result = true;
        if(!ids.isEmpty()) {
            List<String> associatedFiles = new ArrayList<>();
            for (Object id : ids) {
                DataSource dataSource = dataSourceDao.selectOneAndLock(id);
                if (null != dataSource) {
                    String authCreden = dataSource.getAuthCreden();
                    if (StringUtils.isNotBlank(authCreden) && isUrl(authCreden)) {
                        associatedFiles.add(authCreden);
                    }
                }
            }
            result = super.delete(ids) && (permissionDao.deleteBatch(ids) > 0);
            if (result && !associatedFiles.isEmpty()) {
                associatedFiles.forEach(filePath -> {
                    try {
                        AppUtil.removeFile(filePath);
                    } catch (Exception e) {
                        LOG.error("Remove authentication file Error: " + e.getMessage());
                    }
                });
            }
        }
        return result;
    }

    @Override
    public DataSource getDetail(Object id){
        DataSource dataSource = getDao().selectOne(id);
        if(null != dataSource) {
            fillDataSourceWithModel(dataSource, dataSource.getModelId());
            dataSource.setParameter(Json.toJson(dataSource.getParameterMap(), null));
        }
        return dataSource;
    }

    @Override
    public DataSource getDecryptedSimpleOne(Object id) {
        DataSource dataSource = getDao().selectOne(id);
        if(null != dataSource && StringUtils.isNotBlank(dataSource.getAuthCreden()) &&
                !isUrl(dataSource.getAuthCreden())){
            dataSource.setAuthCreden(decodeBase64(dataSource.getAuthCreden()));
        }
        return dataSource;
    }

    @Override
    public File store(MultipartFile authFile, String authType, boolean disposable) throws IOException{
        String newName = (disposable? PERSIST_DISPOSABLE_PREFIX : "") +
                AppUtil.newFileName(authFile.getOriginalFilename());
        File storeFile = new File(conf.getStorePersist(authType) + newName);
        new File(conf.getStorePersist(authType)).mkdirs();
        if (storeFile.createNewFile()) {
            FileUtils.copyInputStreamToFile(authFile.getInputStream(), storeFile);
        }else{
            throw new RuntimeException("Cannot create new file:"+storeFile.getPath()+", check if the system have " +
                    "the right permission");
        }
        return storeFile;
    }

    @Override
    public File store(MultipartFile authFile, String authType) throws IOException {
        return store(authFile, authType, false);
    }

    @Override
    public void addDataSourceModelParamsToMap(Map<String, Object> map, String authEntity, String authCreden, String modelName){
        DataSourceModel modelAssembly = modelDao.selectOneByName(modelName);
        addDataSourceModelParamsToMap(map, authEntity, authCreden,modelAssembly);
    }

    @Override
    public void addDataSourceModelParamsToMap(Map<String, Object> map,
                                              String authEntity, String authCreden, Integer modelId){
        DataSourceModel modelAssembly = modelDao.selectOne(modelId);
        addDataSourceModelParamsToMap(map, authEntity, authCreden,modelAssembly);
    }

    private void addDataSourceModelParamsToMap(Map<String, Object> map,
                                               String authEntity, String authCreden, DataSourceModel modelAssembly){
        if(null != modelAssembly) {
            Map<String, Object> params = modelAssembly.resolveParams();
            if (params.containsKey(Constants.PARAM_AUTH_TYPE)) {
                String authType = String.valueOf(params.get(Constants.PARAM_AUTH_TYPE));
                String authEntityKey = null;
                String authCredenKey = null;
                String authEntityValue = authEntity;
                switch (authType) {
                    case AuthType.KERBERS:
                        authCredenKey = Constants.PARAM_KB_FILE_PATH;
                        authEntityKey = Constants.PARAM_KERBEROS_FILE_PRINCILE;
                        if(StringUtils.isNotBlank(
                                String.valueOf(params.getOrDefault(Constants.PARAM_KERBEROS_HOST_NAME, "")))){
                            authEntityValue = authEntity + IOUtils.DIR_SEPARATOR +
                                    params.getOrDefault(Constants.PARAM_KERBEROS_HOST_NAME, "");
                        }
                        authEntityValue +=  "@" +
                                params.getOrDefault(Constants.PARAM_KERBEROS_REALM_INFO, "");
                        params.put(Constants.PARAM_KERBEROS_BOOLEAN, true);
                        break;
                    case AuthType.LDAP:
                        authEntityKey = Constants.PARAM_LADP_USERNAME;
                        authCredenKey = Constants.PARAM_LADP_PASSWORD;
                        break;
                    case AuthType.KEYFILE:
                        authCredenKey = Constants.PARAM_KEY_FILE_PATH;
                        authEntityKey = Constants.PARAM_DEFAULT_USERNAME;
                        break;
                    case AuthType.DEFAULT:
                        authEntityKey = Constants.PARAM_DEFAULT_USERNAME;
                        authCredenKey = Constants.PARAM_DEFAULT_PASSWORD;
                        break;
                    default:
                        break;
                }
                if(StringUtils.isNotBlank(authEntity) && StringUtils.isNotBlank(authEntityKey)){
                    params.put(authEntityKey, authEntityValue);
                }
                if(StringUtils.isNotBlank(authCreden) && StringUtils.isNotBlank(authCredenKey)){
                    params.put(authCredenKey, authCreden);
                }

            }
            map.putAll(params);
        }
    }


    @Override
    public void fillDataSourceWithModel(DataSource dataSource, Integer modelId){
        Map<String, Object> mergedParams = dataSource.resolveParams();
        Map<String, Object> params = new HashMap<>();
        addDataSourceModelParamsToMap(params, dataSource.getAuthEntity(), dataSource.getAuthCreden(), modelId);
        mergedParams = Stream.of(mergedParams, params)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry ->{
                           Object value = entry.getValue();
                           return value != null? value : "";
                        },
                        (v1, v2) -> v1
                ));
        //Set parameter map
        dataSource.setParameterMap(mergedParams);
    }

    @Override
    public List<DataAuthScope> getPermission(Long id) {
        DataSourcePermission permission = permissionDao.getPermission(id);
        if(null != permission){
            return permission.toDataAuthScopes();
        }
        return Collections.singletonList(DataAuthScope.ALL);
    }

    /**
     * Bind project
     * @param projectId
     * @param dataSourceIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDataSourceToProject(Long projectId, Long... dataSourceIds) {
        if(projectId != null && projectId > 0){
            for (Long dataSourceId : dataSourceIds) {
                if(null != dataSourceId && dataSourceId > 0){
                    DataSource dataSource = dataSourceDao.selectOneAndLock(dataSourceId);
                    bindDataSourceToProject(projectId, dataSource, false);
                }
            }
        }
    }

    private void bindDataSourceToProject(Long projectId, DataSource dataSource, boolean force){
        Long existProjectId = dataSource.getProjectId();
        if(existProjectId != null && existProjectId > 0){
            if(projectId.equals(existProjectId)){
                return;
            }
            if(!force){
                throw new EndPointException("exchange.data_source.project.unbind.not", null, dataSource.getSourceName());
            }
        }else {
            dataSourceDao.bindProject(dataSource.getId(), projectId);
        }
    }
    /**
     *  Base64 encode LDAP password
     */
    private String encodeBase64(String ldapPwd){
        try {
            return CryptoUtils.object2String(ldapPwd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String decodeBase64(String ldapPwd){
        try{
            return (String) CryptoUtils.string2Object(ldapPwd);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    private boolean isUseKb(Map<String, Object> params){
        boolean useKb = false;
        Object v = params.get(PARAM_KERBEROS_BOOLEAN);
        if(null != v){
            String isUseKb = String.valueOf(v);
            useKb = "true".equals(isUseKb);
        }
        return useKb;
    }

    @Override
    protected DataSource queryFilter(DataSource dataSource) {
        if(null != dataSource) {
            String authCreden = dataSource.getAuthCreden();
            if (StringUtils.isBlank(authCreden) || !isUrl(authCreden)) {
                dataSource.setAuthCreden(null);
            }
        }
        return dataSource;
    }

    private boolean isUrl(String input){
        return input.startsWith(URL_PROTOCOL);
    }
}
