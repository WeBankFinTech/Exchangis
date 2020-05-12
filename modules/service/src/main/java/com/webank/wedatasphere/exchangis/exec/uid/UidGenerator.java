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


import org.apache.commons.lang3.tuple.Pair;

/**
 * @author davidhua
 * 2019/10/28
 */
public interface UidGenerator {
    /**
     * generate uid
     * @param nodeId
     * @param execUser
     * @return <type,uid>
     */
    Pair<String, Long> generate(int nodeId, String execUser);
}
