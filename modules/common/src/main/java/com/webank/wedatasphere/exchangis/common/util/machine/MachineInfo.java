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

package com.webank.wedatasphere.exchangis.common.util.machine;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author davidhua
 * 2018/9/12
 */
public class MachineInfo{
    private static final Logger LOG = LoggerFactory.getLogger(MachineInfo.class);
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static Sigar sigar;
    static{
        sigar = new Sigar();
    }
    /**
     * Get rate of used memory
     * @return
     */
    public static double memoryRate(){

        return memoryRate(-1);
    }

    public static long memoryTotal(){
        try{
            return sigar.getMem().getTotal();
        }catch(SigarException e){
            LOG.error(e.getMessage());
        }
        return 0L;
    }

    public static long memoryUsed(){
        try{
            return sigar.getMem().getActualUsed();
        }catch(SigarException e){
            LOG.error(e.getMessage());
        }
        return 0L;
    }

    public static double memoryRate(int scale){
        double result = 0.0;
        try {
            long free = sigar.getMem().getActualFree();
            long used = sigar.getMem().getActualUsed();
            result = (double)used/(double)(used + free);
        }catch(SigarException e){
            LOG.error(e.getMessage());
        }
        if(scale > 0){
            return BigDecimal.valueOf(result).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return result;
    }
    public static double memoryRate0(int scale){
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalMem = osmxb.getTotalPhysicalMemorySize();
        long freeMem = osmxb.getFreePhysicalMemorySize();
        double result = (double)(totalMem - freeMem) / (double) totalMem;
        if(scale > 0){
            BigDecimal bd = BigDecimal.valueOf(result);
            return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return result;
    }

    /**
     * use sigar
     * dependencies:
     * windows: sigar-amd64-winnt.dll、sigar-x86-winnt.dll、sigar-x86-winnt.lib
     * Linux: libsigar-amd64-linux.so、libsigar-x86-linux.so
     * @param scale
     * @return
     */
    public static double cpuRate(int scale){
        double result = 0.0;
        try {
            result = sigar.getCpuPerc().getCombined();
        } catch (SigarException e) {
            LOG.error(e.getMessage());
        }
        if(scale > 0){
            BigDecimal bd = BigDecimal.valueOf(result);
            return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return result;
    }

    public static String getIpAddress(String interfaceName){
        try{
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while(nis.hasMoreElements()){
                NetworkInterface networkInterface = nis.nextElement();
                Enumeration<InetAddress> ias = networkInterface.getInetAddresses();
                if(StringUtils.isBlank(interfaceName) || interfaceName.equalsIgnoreCase(networkInterface.getName())) {
                    while (ias.hasMoreElements()) {
                        InetAddress inetAddress = ias.nextElement();
                        if (inetAddress instanceof Inet4Address &&
                                !inetAddress.getHostAddress().equals(LOCAL_ADDRESS)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static String getIpAddress(){
        return getIpAddress("");
    }

    public static String getProcPath(){
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        pid = pid.substring(0, pid.indexOf('@'));
        try {
            return sigar.getProcArgs(pid)[0];
        } catch (SigarException e) {
            LOG.error(e.getMessage());
            return "";
        }
    }
}
