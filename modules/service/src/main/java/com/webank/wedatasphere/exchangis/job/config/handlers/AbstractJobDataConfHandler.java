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

package com.webank.wedatasphere.exchangis.job.config.handlers;

import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import com.sun.istack.NotNull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.webank.wedatasphere.exchangis.job.JobConstants.CONFIG_DATASOURCEID_NAME;

/**
 * @author enjoyyin
 * 2018/10/29
 */
public abstract class AbstractJobDataConfHandler implements JobDataConfHandler {

    @Override
    @SuppressWarnings("unchecked")
    public final void prePersistHandle(DataConfType dataConfType, DataSource dataSource, Map<String, Object> dataFormParams) {
        try {
            if (dataSource.getId() <= 0) {
                Map<String, Object> parameterMap = dataSource.resolveParams();
                Object pwd = parameterMap.get(Constants.PARAM_LADP_PASSWORD);
                if (null != pwd) {
                    parameterMap.put(Constants.PARAM_LADP_PASSWORD, CryptoUtils.object2String(String.valueOf(pwd)));
                }
                pwd = parameterMap.get(Constants.PARAM_DEFAULT_PASSWORD);
                if(null != pwd){
                    parameterMap.put(Constants.PARAM_DEFAULT_PASSWORD, CryptoUtils.object2String(String.valueOf(pwd)));
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        prePersistValidate(dataFormParams);
        List<DataColumn> columns = new ArrayList<>();
        dataFormParams.putIfAbsent(JobConstants.CONFIG_COLUM_NAME, columns);
        columns = (List<DataColumn>)dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        if(columns.size() <= 0 && isColumnAutoFill()){
            autoFillColumn(columns, dataSource, dataFormParams, dataConfType);
        }
        prePersist0(dataSource, dataFormParams);
        if(DataConfType.READER.equals(dataConfType)){
            prePersistReader(dataSource, dataFormParams);
        }else if(DataConfType.WRITER.equals(dataConfType)){
            prePersistWriter(dataSource, dataFormParams);
        }
        //dataFormParams may contains datasource's ldap password need encoded, so encode it
//        try {
//            Object ldapPwd = dataFormParams.get(Constants.PARAM_LADP_PASSWORD);
//            if (null != ldapPwd) {
//                dataFormParams.put(Constants.PARAM_LADP_PASSWORD, CryptoUtils.object2String(String.valueOf(ldapPwd)));
//            }
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
    }
    @Override
    public Map<String, Object> postGetHandle(DataConfType dataConfType, Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(10);
        //if 'dataConfParams' don't contain property named 'datasourceId', get and return connection parameters from it
        if(null == dataConfParams.get(CONFIG_DATASOURCEID_NAME) ||
            StringUtils.isBlank(String.valueOf(dataConfParams.get(CONFIG_DATASOURCEID_NAME)))){
            for (String paramName : connParamNames()) {
                result.put(paramName, dataConfParams.remove(paramName));
            }
        }else{
            result.put(CONFIG_DATASOURCEID_NAME, dataConfParams.get(CONFIG_DATASOURCEID_NAME));
        }
        result.putAll(postGet0(dataConfParams));
        if(DataConfType.READER.equals(dataConfType)){
            result.putAll(postGetReader(dataConfParams));
        }else if(DataConfType.WRITER.equals(dataConfType)){
            result.putAll(postGetWriter(dataConfParams));
        }
        return result;
    }

    protected void prePersistValidate(Map<String, Object> dataFormParams){

    }

    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams){

    }

    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams){

    }

    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams){

    }

    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams){
        return new HashMap<>(10);
    }

    protected Map<String, Object> postGetReader(Map<String, Object> dataConfParams){
        return new HashMap<>(10);
    }

    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams){
        return new HashMap<>(10);
    }

    protected boolean isColumnAutoFill(){
        return false;
    }
    protected void autoFillColumn(List<DataColumn> columns,  DataSource dataSource,  Map<String, Object> dataFormParams, DataConfType type){
       throw new JobDataParamsInValidException("Don't support filling column list automatically");
    }

    @NotNull
    protected abstract String[] connParamNames();

    boolean isSupport(String value, String[] array){
        boolean isSupport = false;
        for(String item: array){
            if(item.equalsIgnoreCase(value)){
                isSupport = true;
                break;
            }
        }
        return isSupport;
    }

    String avoidSeparator(String input){
        String result = input;
        result = result.replace(String.valueOf(IOUtils.DIR_SEPARATOR), "");
        result = result.replace(String.valueOf(IOUtils.DIR_SEPARATOR_UNIX), "");
        result = result.replace(String.valueOf(IOUtils.DIR_SEPARATOR_WINDOWS), "");
        result = result.replace(String.valueOf(IOUtils.LINE_SEPARATOR), "");
        result = result.replace(String.valueOf(IOUtils.LINE_SEPARATOR_UNIX), "");
        result = result.replace(String.valueOf(IOUtils.LINE_SEPARATOR_WINDOWS), "");
        return result;
    }

    <T>T safeTypeConvert(Object obj, Class<T> clazz){
        if( obj != null) {
            T retObject = null;
            try {
                retObject = clazz.cast(obj);
            }catch (ClassCastException e){
                //ignore
            }

            return retObject;
        }
        return null;
    }
}
