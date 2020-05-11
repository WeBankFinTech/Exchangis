#!/bin/bash
#
# Copyright 2020 WeBank
#
# Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
SLEEP_TIMEREVAL_S=2

if [ "x$1" == "x" ]; then
    echo "you should add a process ID of the task which needs to be killed!" >&2
    exit 1
fi


status_pid(){
    local p_tree=`pstree $1 -p`
    if [ -n "${p_tree}" ]; then
        echo "Task $1 is still alive..."
        return 0
    else
        echo "Task $1 has been killed"
        return 1
    fi
}

wait_for_shutdown(){
    local now_s=`date '+%s'`
    local stop_s=$((${now_s} + $1))
    while [ ${now_s} -le ${stop_s} ]; do
        status_pid $2
        if [ $? -eq 1 ]; then
            return 0
        fi
        sleep ${SLEEP_TIMEREVAL_S}
        now_s=`date '+%s'`
    done
    return 1
}

kill_task(){
    echo "start to kill task whose pid is ${1}"
    case "`uname`" in
        CYGWIN*) taskkill /T /PID "$1";;
        *) pstree "$1" -p | awk -F "[()]" '{print $2}' | sudo xargs kill -SIGTERM;;
    esac
    echo "wait for shutdown ..."
    wait_for_shutdown 5 $1
    if [ $? -eq 1 ]; then
        local status=`ps -aux | grep $1 | grep -v grep | awk '{print $8}'`
        if [ "$status" == "Z" ]; then
          echo "this task seems to be blocked, force to kill it"
          case "`uname`" in
              CYGWIN*) taskkill /F /T /PID "$1";;
              *) pstree "$1" -p | awk -F "[()]" '{print $2}' | sudo xargs kill -9;;
          esac
        fi
    fi
}

kill_task $1
