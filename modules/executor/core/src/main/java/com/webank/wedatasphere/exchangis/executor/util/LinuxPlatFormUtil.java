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

package com.webank.wedatasphere.exchangis.executor.util;

import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author davidhua
 * 2019/11/16
 */
public class LinuxPlatFormUtil {

    public static final String DEFAULT_HADOOP_USER = "hadoop";
    private static final Pattern LINUX_USER_INFO_PATTERN = Pattern.compile("([\\S]+)?=([\\d]+)");
    public static Triple<Boolean, Integer, Integer> existUser(String username){
        String linuxIdCommand = "id ${userName}";
        RunShell runShell = new RunShell(PatternInjectUtils.inject(linuxIdCommand,
                new String[]{username}));
        if(runShell.run() == 0){
            String result = runShell.getResult();
            String uid = null, gid = null;
            Matcher matcher = LINUX_USER_INFO_PATTERN.matcher(result);
            while(matcher.find()){
                String name = matcher.group(1);
                if("uid".equalsIgnoreCase(name)){
                    uid = matcher.group(2);
                }else if("gid".equalsIgnoreCase(name)){
                    gid = matcher.group(2);
                }
            }
            if(StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(gid)){
                return new MutableTriple<>(true, Integer.valueOf(uid), Integer.valueOf(gid));
            }
        }
        return new MutableTriple<>(false, null, null);
    }

    public static Pair<Boolean, String> createUser(String username, Integer uid, Integer gid){
        String linuxUserAddCommand = "sudo useradd ${userName}";
        List<String> params = new ArrayList<>();
        params.add(username);
        if(Optional.ofNullable(uid).filter(e -> e > 0).isPresent()){
            linuxUserAddCommand += " -u ${uid}";
            params.add(String.valueOf(uid));
        }
        if(Optional.ofNullable(gid).filter(e -> e > 0).isPresent()){
            linuxUserAddCommand += " -g ${gid}";
            params.add(String.valueOf(gid));
        }
        String[] paramArray = new String[params.size()];
        params.toArray(paramArray);
        RunShell runShell = new RunShell(PatternInjectUtils.inject(linuxUserAddCommand, paramArray));
        return runShell.run() == 0? new MutablePair<>(true, null) : new MutablePair<>(false, runShell.getResult());
    }

    public static Pair<Integer, Integer> currentUser(){
        String username = System.getProperty("user.name", "");
        if(StringUtils.isNotBlank(username)){
            Triple<Boolean, Integer, Integer> triple = existUser(username);
            if(triple.getLeft()){
                return new MutablePair<>(triple.getMiddle(), triple.getRight());
            }
        }
        return new MutablePair<>(null, null);
    }
    public static Pair<Boolean, String> deleteUser(String username){
        String linuxUserDeleteCommand = "sudo userdel ${userName}";
        RunShell runShell = new RunShell(PatternInjectUtils.inject(linuxUserDeleteCommand, new String[]{username}));
        return runShell.run() == 0?new MutablePair<>(true, null) : new MutablePair<>(false, runShell.getResult());
    }

}
