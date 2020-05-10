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


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author davidhua
 * 2020/1/3
 */
public class MemUtils {
    private static final Map<String, StoreUnit> UNIT_MAP = new HashMap<>();
    static{
        UNIT_MAP.put("G", StoreUnit.GB);
        UNIT_MAP.put("GB", StoreUnit.GB);
        UNIT_MAP.put("B", StoreUnit.B);
        UNIT_MAP.put("M", StoreUnit.MB);
        UNIT_MAP.put("MB", StoreUnit.MB);
        UNIT_MAP.put("K", StoreUnit.KB);
        UNIT_MAP.put("KB", StoreUnit.KB);
    }
    public static long convertToGB(long size, String unitFlag){
        if(size < 0){
            return -1L;
        }
        if(StringUtils.isNotBlank(unitFlag)){
            StoreUnit storeUnit = UNIT_MAP.get(unitFlag.trim().toUpperCase());
            if(null != storeUnit){
                 return storeUnit.toGB(size);
            }
        }
        return -1L;
    }

    public static long convertToMB(long size, String unitFlag){
        if(size < 0){
            return -1L;
        }
        if(StringUtils.isNotBlank(unitFlag)){
            StoreUnit storeUnit = UNIT_MAP.get(unitFlag.trim().toUpperCase());
            if(null != storeUnit){
                return storeUnit.toMB(size);
            }
        }
        return -1L;
    }

    public static long convertToByte(long size, String unitFlag){
        if(size < 0){
            return -1L;
        }
        if(StringUtils.isNotBlank(unitFlag)){
            StoreUnit storeUnit = UNIT_MAP.get(unitFlag.trim().toUpperCase());
            if(null != storeUnit){
                return storeUnit.toB(size);
            }
        }
        return -1L;
    }
    public enum StoreUnit {
        /**
         * byte
         */
        B {
            @Override
            public long toB(long s){
                return s;
            }

            @Override
            public long toKB(long s){
                return s/(C1/C0);
            }

            @Override
            public long toMB(long s) {
                return s/(C2/C0);
            }

            @Override
            public long toGB(long s) {
                return s/(C3/C0);
            }

            @Override
            public long toTB(long s) {
                return s/(C4/C0);
            }
        },
        /**
         * kb
         */
        KB{
            @Override
            public long toB(long s){
                return x(s, C1/C0, Long.MAX_VALUE/(C1/C0));
            }

            @Override
            public long toKB(long s){
                return s;
            }

            @Override
            public long toMB(long s) {
                return s/(C2/C1);
            }

            @Override
            public long toGB(long s) {
                return s/(C3/C1);
            }

            @Override
            public long toTB(long s) {
                return s/(C4/C0);
            }
        },
        MB{
            @Override
            public long toB(long s){
                return x(s, C2/C0, Long.MAX_VALUE/(C2/C0));
            }

            @Override
            public long toKB(long s){
                return x(s, C2/C1, Long.MAX_VALUE/(C2/C1));
            }

            @Override
            public long toMB(long s) {
                return s;
            }

            @Override
            public long toGB(long s) {
                return s/(C3/C2);
            }

            @Override
            public long toTB(long s) {
                return s/(C4/C2);
            }
        },
        GB{
            @Override
            public long toB(long s){
                return x(s, C3/C0, Long.MAX_VALUE/(C3/C0));
            }

            @Override
            public long toKB(long s){
                return x(s, C3/C1, Long.MAX_VALUE/(C3/C1));
            }

            @Override
            public long toMB(long s) {
                return x(s, C3/C2, Long.MAX_VALUE/(C3/C2));
            }

            @Override
            public long toGB(long s) {
                return s;
            }

            @Override
            public long toTB(long s) {
                return s/(C4/C3);
            }
        },
        TB{
            @Override
            public long toB(long s){
                return x(s, C4/C0, Long.MAX_VALUE/(C4/C0));
            }

            @Override
            public long toKB(long s){
                return x(s, C4/C1, Long.MAX_VALUE/(C4/C1));
            }

            @Override
            public long toMB(long s) {
                return x(s, C4/C2, Long.MAX_VALUE/(C4/C2));
            }

            @Override
            public long toGB(long s) {
                return x(s, C4/C3, Long.MAX_VALUE/(C4/C3));
            }

            @Override
            public long toTB(long s) {
                return s;
            }
        };

        public long toB(long s){
            throw new AbstractMethodError();
        }

        public long toKB(long s){
            throw new AbstractMethodError();
        }

        public long toMB(long s){
            throw new AbstractMethodError();
        }

        public long toGB(long s){
            throw new AbstractMethodError();
        }

        public long toTB(long s){
            throw new AbstractMethodError();
        }
    }

    static long x(long d, long m, long over){
        if(d >  over){
            return Long.MAX_VALUE;
        }
        if(d < -over){
            return Long.MIN_VALUE;
        }
        return d * m;
    }
    static final long C0 = 1L;
    static final long C1 = C0 * 1024L;
    static final long C2 = C1 * 1024L;
    static final long C3 = C2 * 1024L;
    static final long C4 = C3 * 1024L;

}
