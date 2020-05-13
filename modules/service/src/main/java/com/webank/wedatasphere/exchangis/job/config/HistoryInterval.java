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

package com.webank.wedatasphere.exchangis.job.config;

import com.webank.wedatasphere.exchangis.common.util.DateTool;
import com.webank.wedatasphere.exchangis.job.domain.JobHistoryReq;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static com.webank.wedatasphere.exchangis.common.util.DateTool.TIME_REGULAR_PATTERN;

@Component
public class HistoryInterval {
    public static final String DAY = "DAY";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    private static final String[] TIME_INTERVAL = new String[]{DAY,HOUR,MINUTE};

    private static final String TIME_PLACEHOLDER_DATE_TIME = "${yyyy-MM-dd HH:mm:ss}";
    private static final String TIME_PLACEHOLDER_TIMESTAMP = "${timestamp}";
    private static final String[] TIME_PLACEHOLDER = new String[]
            {TIME_PLACEHOLDER_DATE_TIME, TIME_PLACEHOLDER_TIMESTAMP};


    public boolean checkTime(JobInfo jobInfo, JobHistoryReq req){
        boolean flag = false;
        String unit = req.getUnit();
        if(!StringUtils.isEmpty(jobInfo.getJobConfig()) && !StringUtil.isEmpty(unit) && req.getStep() != 0){
            for(String time : TIME_INTERVAL){
                if(req.getUnit().equals(time)){
                    flag = true;
                    break;
                }
            }
            if(flag){
                flag = false;
                String[] placeholders = DateTool.TIME_PLACEHOLDER;
                if(unit.equals(HOUR) || unit.equals(MINUTE)){
                    placeholders = TIME_PLACEHOLDER;
                }
                for(String p : placeholders){
                    if(jobInfo.getJobConfig().contains(p)){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    Matcher matcher = TIME_REGULAR_PATTERN.matcher(jobInfo.getJobConfig());
                    return matcher.find();
                }
            }
        }
        return flag;
    }

    public boolean checkStep(JobHistoryReq req){
        boolean flag = true;
        if(req.getUnit().equals(DAY)){
            if(req.getStep()> 30 || req.getStep() <= 0){
                flag = false;
            }
        }
        if(req.getUnit().equals(HOUR)){
            if(req.getStep()> 24 || req.getStep() <= 0){
                flag = false;
            }
        }
        if(req.getUnit().equals(MINUTE)){
            if(req.getStep()> 60 || req.getStep() <= 0){
                flag = false;
            }
        }
        return flag;
    }
}
