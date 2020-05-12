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

package com.webank.wedatasphere.exchangis.exec.uid;

import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeUserDao;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User can implement custom generator
 * @author davidhua
 * 2019/10/28
 */
@Component
public class PlatformUidGenerator implements UidGenerator{

    public static final Integer DEFAULT_EXEC_USER_GROUP_ID = 0;
    @Resource
    private ExecNodeUserDao execNodeUserDao;
    private enum UserType{
        /**
         * User type defined
         */
        SUPER_USER, PLATFORM_USER,
        DATA_MANAGER_USER, DATA_READ_USER,
        APP_USER, REAL_USER,
        DATA_MANAGER_USER_S,DATA_READ_USER_S
    }

    @Override
    public Pair<String, Long> generate(int nodeId, String execUser) {
        return new MutablePair<>(UserType.REAL_USER.toString(), 0L);
    }

}
