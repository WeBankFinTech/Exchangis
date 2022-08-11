/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.engine.resource.uri;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Engine resource stream handler factory (support specific schemas)
 */
public class ResourceURLStreamHandlerFactory implements URLStreamHandlerFactory {

    /**
     * Support schemas
     */
    private final List<String> supportSchemas = new ArrayList<>();

    /**
     * Stream handler
     */
    private final URLStreamHandler defaultStreamHandler;

    public ResourceURLStreamHandlerFactory(String... schemas){
        supportSchemas.addAll(Arrays.asList(schemas));
        this.defaultStreamHandler = new ResourceURLStreamHandler();
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (supportSchemas.stream().anyMatch( schema -> schema.equals(protocol))){
            return this.defaultStreamHandler;
        }
        return null;
    }
}
