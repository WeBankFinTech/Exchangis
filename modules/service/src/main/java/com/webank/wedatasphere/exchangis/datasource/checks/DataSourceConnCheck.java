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

import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;

import java.io.File;


/**
 * @author davidhua
 * 2018/9/03
 * DataSource Test Interface
 */
public interface DataSourceConnCheck {
    String PREFIX = "DatSourceConnCheck-";
    Integer CONNECT_TIMEOUT_IN_SECONDS = 5;
    /**
     * Validate the data source's model assembly
     * @param md data source model assembly
     * @throws Exception if validate fail , throw an Exception
     */
    void validate(DataSourceModel md) throws Exception;

    /**
     * Check main method
     * @param ds data source
     * @param file associate file, not must be required
     */
    void check(DataSource ds, File file) throws Exception;

}
