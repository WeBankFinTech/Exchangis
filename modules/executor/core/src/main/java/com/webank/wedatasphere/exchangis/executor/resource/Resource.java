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

package com.webank.wedatasphere.exchangis.executor.resource;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author davidhua
 * 2019/9/10
 */
public class Resource {

    private long resourceId;

    private AtomicLong memByte;
    private AtomicLong cpuCore;

    public Resource(long resourceId, long cpuCore, long memByte){
        this.resourceId = resourceId;
        this.memByte = new AtomicLong(memByte);
        this.cpuCore = new AtomicLong(cpuCore);
    }

    public Resource(long resourceId){
        this(resourceId, 0, 0);
    }

    void collect(Resource resource){
        this.memByte.addAndGet(resource.getMemByte());
        this.cpuCore.addAndGet(resource.getCpuCore());
    }

    void allocate(Resource resource){
        this.memByte.addAndGet(-resource.getMemByte());
        this.cpuCore.addAndGet(-resource.getCpuCore());
    }

    public void addMem(long memByte){
        this.memByte.addAndGet(memByte);
    }

    public void addCpu(long cpuCore){
        this.cpuCore.addAndGet(cpuCore);
    }

    public long getMemByte() {
        return memByte.get();
    }

    public void setMemByte(long memByte) {
        this.memByte.set(memByte);
    }

    public long getCpuCore() {
        return cpuCore.get();
    }

    public void setCpuCore(int cpuCore) {
        this.cpuCore.set(cpuCore);
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }
}
