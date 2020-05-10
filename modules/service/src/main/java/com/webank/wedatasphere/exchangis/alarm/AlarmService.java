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

import java.util.List;

/**
 * @author davidhua
 * 2020/4/20
 */
public interface AlarmService {
    /**
     * Get default alarm user list
     * @return list
     */
    List<String> getDefaultAlarmUsers();

    /**
     * Send alarm
     * @param alarmTitle title
     * @param alarmLevel level
     * @param alarmInfo info
     * @param alarmReceivers receivers
     */
    void doSend(String alarmTitle, Integer alarmLevel, String alarmInfo, List<String> alarmReceivers);
}
