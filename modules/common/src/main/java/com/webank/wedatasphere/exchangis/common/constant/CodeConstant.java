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

package com.webank.wedatasphere.exchangis.common.constant;


/**
 * @author devendeng on 2018/9/18.
 */
public class CodeConstant {

    /**
     * Warning
     */
    public static final int WARNING_MSG = 11111;

    /**
     * Login Fail
     */
    public static final int LOGIN_FAIL = 80000;
    /**
     * Parameter error
     */
    public static final int PARAMETER_ERROR=30001;

    /**
     * Task not exists
     */
    public static final int TASK_NOT_EXISTS=30002;
    /**
     * Task allocate failed
     */
    public static final int TASK_ALLOCATE_FAILD =80001;

    /**
     * Task execution error
     */
    public static final int EXECUTER_ERROR=50001;

    /**
     * System error
     */
    public static final int SYS_ERROR=20001;

    /**
     * Authentication error
     */
    public static final int AUTH_ERROR=40001;

    /**
     * Data source connection error
     */
    public static final int DATASOURCE_CONN_ERROR = 10001;
    /**
     * Fail to add data source
     */
    public static final int DATASOURCE_ADD_ERROR = 10002;
    /**
     * Exist jobs related data source
     */
    public static final int DATASOURCE_EXIST_JOBS = 10003;
    /**
     * Cannot find checker for data source
     */
    public static final int DATASOURCE_CHECK_NOT_EXISTS = 10004;
    /**
     * Cannot find file
     */
    public static final int FILE_NOT_FOUND = 60001;
    /**
     * Fail to add data source model
     */
    public static final int MODELASSEMBLIY_ADD_ERROR = 10005;
}
