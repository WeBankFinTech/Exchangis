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

package com.webank.wedatasphere.exchangis.datasource.domain;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data source permissions
 * @author davidhua
 * 2020/4/5
 */
public class DataSourcePermission {
    /**
     * ID of data source
     */
    private long dataSourceId;

    /**
     * Read permission
     */
    private boolean readable = false;

    /**
     * Write permission
     */
    private boolean writeable = false;

    /**
     * Edit permission
     */
    private boolean editable = false;
    /**
     * Execute permission
     */
    private boolean executable = false;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Modify time
     */
    private Date modifyTime;

    public DataSourcePermission(){

    }

    public DataSourcePermission(long dataSourceId){
        this.dataSourceId = dataSourceId;
    }
    public DataSourcePermission(long dataSourceId, List<String> authScopes){
        this.dataSourceId = dataSourceId;
        authScopes.forEach(authScope ->{
            try {
                DataAuthScope scope = DataAuthScope.valueOf(authScope);
                if(scope.equals(DataAuthScope.DATA_READ)){
                    this.readable = true;
                }else if(scope.equals(DataAuthScope.WRITE)){
                    this.editable = true;
                }else if(scope.equals(DataAuthScope.EXECUTE)){
                    this.executable = true;
                }else if(scope.equals(DataAuthScope.DATA_WRITE)){
                    this.writeable = true;
                }
            }catch(Exception e){
                //Ignore
            }
        });
    }
    public long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    public boolean isExecutable() {
        return executable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public List<DataAuthScope> toDataAuthScopes(){
        List<DataAuthScope> scopes = new ArrayList<>();
        if(readable){
            scopes.add(DataAuthScope.DATA_READ);
        }
        if(writeable){
            scopes.add(DataAuthScope.DATA_WRITE);
        }
        if(editable){
            scopes.add(DataAuthScope.WRITE);
        }
        if(executable){
            scopes.add(DataAuthScope.EXECUTE);
        }
        return scopes;
    }
}
