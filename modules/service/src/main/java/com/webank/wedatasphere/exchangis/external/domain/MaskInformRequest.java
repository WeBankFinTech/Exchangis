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

package com.webank.wedatasphere.exchangis.external.domain;

/**
 * The request entity sent by mask server
 * @author davidhua
 * 2019/9/27
 */
public class MaskInformRequest {

    /**
     * Source database (masked)
     */
    private String maskDatabase;

    /**
     * Source table (masked)
     */
    private String maskTable;

    /**
     * Destination database
     */
    private String destDatabase;

    /**
     * Destination table
     */
    private String destTable;


}
