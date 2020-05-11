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

package com.webank.wedatasphere.exchangis.datasource.checks;

import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;
import java.util.Set;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
/**
 * @author davidhua
 * 2018/9/5
 */
public abstract class AbstractDataSourceConnCheck implements DataSourceConnCheck{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataSourceConnCheck.class);

    boolean isUseKb(Map<String, Object> params){
        boolean useKb = false;
        Object v = params.get(PARAM_KERBEROS_BOOLEAN);
        if(null != v){
            String isUseKb = String.valueOf(v);
            useKb = "true".equals(isUseKb);
        }
        return useKb;
    }

    void  validateKb(Map<String, Object> params) throws Exception{
        Set<String> keys = params.keySet();
        if(isUseKb(params)){
            //ignore the condition that 'file == null', because file's path can be given by 'DataSource'
            if( !keys.contains(PARAM_KERBEROS_REALM_INFO)) {
                throw new Exception(PARAM_KERBEROS_HOST_NAME + " or " + PARAM_KERBEROS_REALM_INFO + " cannot be found");
            }
        }
    }

    File getAuthFileFromDataSource(DataSource ds, String authType, String storeTmp){
        if(null != ds && StringUtils.isNotBlank(ds.getAuthCreden())){
            String authUri = ds.getAuthCreden();
            String newName = authUri;
            if(newName.lastIndexOf(String.valueOf(IOUtils.DIR_SEPARATOR_UNIX)) > 0){
                newName = newName.substring(newName.lastIndexOf(String.valueOf(IOUtils.DIR_SEPARATOR_UNIX)) + 1);
            }
            String path = storeTmp + AppUtil.newFileName(newName);
            AppUtil.downloadFile(authUri, path);
            return new File(path);
        }
        return null;
    }

    public static DataSourceConnCheck getConnCheck(String type, HttpServletResponse resp){
        DataSourceConnCheck connCheck;
        try{
            if(null == TypeEnums.type(type)){
                throw new IllegalArgumentException("Can not found type name: [" + type+"]");
            }
            connCheck = AppUtil.getBean(DataSourceConnCheck.PREFIX + type.toLowerCase(),
                    DataSourceConnCheck.class);
        }catch(Exception e){
            logger.error(e.getLocalizedMessage());
            if (null != resp){
                resp.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
            connCheck = null;
        }
        return connCheck;
    }
}
