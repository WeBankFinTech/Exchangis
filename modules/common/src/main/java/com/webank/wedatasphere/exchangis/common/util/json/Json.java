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

package com.webank.wedatasphere.exchangis.common.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author davidhua
 * Json utils
 * 2018/9/3
 */
public class Json {
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";
    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    private static ObjectMapper mapper;

    static{
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //empty beans allowed
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //cancel to scape non ascii
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }
    private Json(){}

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<?> clazz, Class<?>... parameters){
        if(StringUtils.isNotBlank(json)){
            try{
                if(parameters.length > 0){
                    return (T)mapper.readValue(json, mapper.getTypeFactory().constructParametricType(clazz, parameters));
                }
                if(json.startsWith(PREFIX)
                        && json.endsWith(SUFFIX)){
                    JavaType javaType = mapper.getTypeFactory()
                            .constructParametricType(ArrayList.class, clazz);
                    return mapper.readValue(json, javaType);
                }
                return (T)mapper.readValue(json, clazz);
            } catch (Exception e) {
                logger.info(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static <T> T fromJson(InputStream stream, Class<?> clazz, Class<?>... parameters){
        StringBuilder builder = new StringBuilder();
        String jsonStr = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            while((jsonStr = reader.readLine()) != null){
                builder.append(jsonStr);
            }
            reader.close();
        }catch(Exception e){
            logger.info(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return fromJson(builder.toString(), clazz, parameters);
    }

    public static String toJson(Object obj, Class<?> model){
        ObjectWriter writer = mapper.writer();
        if(null != obj){
            try{
                if(null != model){
                    writer = writer.withView(model);
                }
                return writer.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                logger.info(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
