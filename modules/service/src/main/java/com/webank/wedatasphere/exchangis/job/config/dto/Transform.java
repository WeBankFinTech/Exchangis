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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2018/10/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Transform {
    private String name;

    private Parameter parameter;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Parameter{
        private int columnIndex;
        private List<String> paras = new ArrayList<>();

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public List<String> getParas() {
            return paras;
        }

        public void setParas(List<String> paras) {
            this.paras = paras;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
