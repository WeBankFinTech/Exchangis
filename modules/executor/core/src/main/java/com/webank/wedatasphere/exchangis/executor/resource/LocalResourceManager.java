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

import com.webank.wedatasphere.exchangis.common.util.machine.MachineInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author davidhua
 * 2019/9/10
 */
@Component
public class LocalResourceManager implements ResourceManager {
    private static Logger LOG = LoggerFactory.getLogger(LocalResourceManager.class);
    @javax.annotation.Resource
    private ResourceConfiguration resourceConfiguration;

    private ReentrantLock resourceLock = new ReentrantLock();

    /**
     *  resource held
     */
    private Resource resWareHouse;

    private ConcurrentHashMap<String, Resource> allocated = new ConcurrentHashMap<>();
    @PostConstruct
    public void init(){
        resWareHouse = new Resource(-1L, 0, MachineInfo.memoryTotal() - MachineInfo.memoryUsed());
    }
    @Override
    public boolean allocate(Resource resource) {
        resourceLock.lock();
        try{
            if(allocated.size() <= 0){
                resWareHouse.setMemByte(MachineInfo.memoryTotal() - MachineInfo.memoryUsed());
            }
            if(resWareHouse.getMemByte() >= resource.getMemByte()
                     && resWareHouse.getCpuCore() >= resource.getCpuCore()){
                long memRequire = resource.getMemByte();
                long totalMem = MachineInfo.memoryTotal();
                double actualRate = (double)(MachineInfo.memoryUsed() + memRequire)/(double)totalMem;
                double virtualRate = (double)(totalMem - resWareHouse.getMemByte() + memRequire)/(double)totalMem;
                LOG.info("ActualRate:[" + actualRate +"], virtualRate:[" + virtualRate +"]," +
                        " threshold:["+resourceConfiguration.getThresholdMem() + "]");
                if(Math.max(actualRate, virtualRate) >= resourceConfiguration.getThresholdMem()){
                    return false;
                }

                resWareHouse.allocate(resource);
                return null == allocated.putIfAbsent(String.valueOf(resource.getResourceId()), resource);
            }
            return false;
        }finally{
            resourceLock.unlock();
        }
    }

    @Override
    public void collect(Resource resource) {
        if(null != allocated.remove(String.valueOf(resource.getResourceId()))){
            resWareHouse.collect(resource);
            if(allocated.size() <= 0){
                resourceLock.lock();
                try {
                    resWareHouse.setMemByte(MachineInfo.memoryTotal() - MachineInfo.memoryUsed());
                }finally{
                    resourceLock.unlock();
                }
            }
        }
    }
}
