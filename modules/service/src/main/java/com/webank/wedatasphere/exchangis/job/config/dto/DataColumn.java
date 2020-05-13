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

package com.webank.wedatasphere.exchangis.job.config.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data source column
 * @author enjoyyin
 * 2018/10/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataColumn{
    private String name;

    private String type;

    private Integer index;
    public DataColumn(){

    }

    public DataColumn(String name, String type){
        this.name = name;
        this.type = type;
    }

    public DataColumn(String name, String type, Integer index){
        this.name = name;
        this.type = type;
        this.index = index;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
