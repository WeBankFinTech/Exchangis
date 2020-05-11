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

package com.webank.wedatasphere.exchangis.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author davidhua
 * 2018/9/14
 */
@Component
@PropertySource("classpath:machine-load-cfg.properties")
public class MachineLoadConf {

    @Value("${heartbeat.avail.interval}")
    private long heartBeatAvailInterval;

    @Value("${load.cpu.weight}")
    private int cpuWeight;

    @Value("${load.memory.weight}")
    private int memWeight;

    @Value("${load.cpu.threshold}")
    private double cpuThreshold;

    @Value("${load.memory.threshold}")
    private double memThreshold;

    public long getHeartBeatAvailInterval(){
        return heartBeatAvailInterval;
    }

    public int getCpuWeight(){
        return cpuWeight;
    }

    public int getMemWeight(){
        return memWeight;
    }

    public double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }

    public double getMemThreshold() {
        return memThreshold;
    }

    public void setMemThreshold(double memThreshold) {
        this.memThreshold = memThreshold;
    }
}
