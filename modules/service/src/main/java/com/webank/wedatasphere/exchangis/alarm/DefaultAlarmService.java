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

package com.webank.wedatasphere.exchangis.alarm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author davidhua
 * 2020/4/20
 */
@Component
public class DefaultAlarmService implements AlarmService{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAlarmService.class);
    @Override
    public List<String> getDefaultAlarmUsers() {
        return new ArrayList<>();
    }

    @Override
    public void doSend(String alarmTitle, Integer alarmLevel, String alarmInfo, List<String> alarmReceivers) {
        LOG.info(String.format("AlarmTitle: [%s], AlarmLevel: [%s], AlarmInfo: [%s], AlarmReceivers: [%s]",
                alarmTitle, alarmLevel, alarmInfo, StringUtils.join(alarmReceivers, ",")));
    }
}
