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

package com.webank.wedatasphere.exchangis.exec.dao;



import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeTab;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author davidhua
 * 2019/12/29
 */
public interface ExecNodeTabDao {

    /**
     * Get tab id list
     * @param nodeId
     * @return
     */
    List<Integer> getTabIdsByExecNode(Integer nodeId);

    /**
     * Insert
     * @param batch
     */
    void insertBatch(List<ExecNodeTab> batch);

    /**
     * Delete
     * @param nodeId
     * @param tabIds
     */
    void deleteBatch(@Param("nodeId") Integer nodeId, @Param("ids") List<Integer> tabIds);

}
