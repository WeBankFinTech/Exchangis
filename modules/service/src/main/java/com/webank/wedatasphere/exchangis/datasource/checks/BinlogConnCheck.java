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
import org.springframework.stereotype.Service;

import java.io.File;

import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

/**
 * @author davidhua
 * 2020/2/26
 */
@Service(PREFIX + "binlog")
public class BinlogConnCheck extends AbstractDataSourceConnCheck{
    @Override
    public void validate(DataSourceModel md) throws Exception {

    }

    @Override
    public void check(DataSource ds, File file) throws Exception {

    }
}
