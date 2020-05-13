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

package com.webank.wedatasphere.exchangis.job.config;


import java.util.HashMap;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/4/1
 */
public enum TransportType {
    //
    STREAM("stream"),

    RECORD("record");

    private static Map<String, TransportType> typeMap = new HashMap<>();

    private String type;

    TransportType(String type){
        this.type = type;
    }

    public String v(){
        return this.type;
    }
    public static TransportType type(String type){
        TransportType result = typeMap.get(type != null? type.toLowerCase(): type);
        return result == null ? TransportType.RECORD : result;
    }

    static{
        typeMap.put(STREAM.type, STREAM);
        typeMap.put(RECORD.type, RECORD);
    }
}
