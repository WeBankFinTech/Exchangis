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

package com.webank.wedatasphere.exchangis.executor.domain;

/**
 *
 * @author devendeng
 * @date 2018/9/4
 */
public enum ExecuteStatus {
    /**
     * COMMIT:1, RUNNING:2, KILL:3, SUCCESS:4, FAIL:5, TIMEOUT:6
     */
    NONE(-1), COMMIT(1),RUNNING(2),KILL(3),SUCCESS(4),FAILD(5), RUNNING_TIMEOUT(6);
    private int value;
    ExecuteStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
