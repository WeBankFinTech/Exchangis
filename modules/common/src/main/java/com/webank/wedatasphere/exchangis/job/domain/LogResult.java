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

package com.webank.wedatasphere.exchangis.job.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by devendeng on 2018/9/13.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogResult {
    private int startLineNum;
    private int endLineNum;
    private String logContent;
    private boolean isEnd;

    public LogResult() {}

    public LogResult(int startLineNum, int endLineNum, String logContent, boolean isEnd) {
        this.startLineNum = startLineNum;
        this.endLineNum = endLineNum;
        this.logContent = logContent;
        this.isEnd = isEnd;
    }

    public int getStartLineNum() {
        return startLineNum;
    }

    public void setStartLineNum(int startLineNum) {
        this.startLineNum = startLineNum;
    }

    public int getEndLineNum() {
        return endLineNum;
    }

    public void setEndLineNum(int endLineNum) {
        this.endLineNum = endLineNum;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
