package com.webank.wedatasphere.exchangis.job.utils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Mem utils
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