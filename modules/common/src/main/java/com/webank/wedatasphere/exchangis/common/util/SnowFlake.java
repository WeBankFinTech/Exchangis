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

package com.webank.wedatasphere.exchangis.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * snowflake
 * @author davidhua
 * 2019/6/4
 */
public class SnowFlake {
    private static final Logger logger = LoggerFactory.getLogger(SnowFlake.class);

    private static long workerIdBits = 5L;
    private static long dataCenterIdBits = 5L;

    /**
     * Counter
     */
    private static long sequenceBits = 12L;
    /**
     * Shift left
     */
    private static long workerIdShift = sequenceBits;
    private static long dataCenterIdShift = sequenceBits + workerIdBits;
    private static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static long maxDataCenterId =  -1L ^ (-1L << dataCenterIdBits);
    private long lastTimeStamp = -1L;

    private long workerId;
    private long dataCenterId;
    private long sequence=0L;

    private long startTime = 1238434978657L;
    public SnowFlake(long dataCenterId, long workerId, long startTime){
        //check
        if(workerId > maxWorkerId || workerId < 0){
            throw new IllegalArgumentException("worker Id can't be greater than " + maxWorkerId + " or less than 0");
        }
        if(dataCenterId > maxDataCenterId || dataCenterId < 0){
            throw new IllegalArgumentException("dataCenter Id can't be greater than " + maxDataCenterId + " or less than 0");
        }
        this.workerId = workerId;
        this.startTime = startTime;
        this.dataCenterId = dataCenterId;
    }

    public static long generateId(long timestamp, long startTime , long dataCenterId, long workerId){
        return ((timestamp - startTime) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift);
    }

    public static long generateId(long timestamp, long startTime){
        return ((timestamp - startTime) << timestampLeftShift) | (maxDataCenterId << dataCenterIdShift) | (maxWorkerId << workerIdShift);
    }
    /**
     * Application lock
     */
    public synchronized long nextId(){
        long timestamp = timeGen();
        if(timestamp < lastTimeStamp){
            logger.info("clock is moving backwards.Rejecting request until " + lastTimeStamp);
        }
        if(lastTimeStamp == timestamp){
            sequence = (sequence + 1) & sequenceMask;
            if(sequence == 0){
                timestamp = tillNextMills(lastTimeStamp);
            }
        }else{
            sequence = 0L;
        }
        lastTimeStamp = timestamp;
        return ((timestamp - startTime) << timestampLeftShift)|(dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    private long tillNextMills(long lastTimeStamp){
        long timestamp =  timeGen();
        while(timestamp <= lastTimeStamp){
            timestamp = timeGen();
        }
        return timestamp;
    }
    private long timeGen(){
        return System.currentTimeMillis();
    }

}
