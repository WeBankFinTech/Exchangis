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
# Launcher for modules, provided start/stop functions

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
ENV_FILE="${DIR}/env.properties"
SHELL_LOG="${DIR}/command.log"
USER_DIR="${DIR}/../"
EXCHANGIS_LIB_PATH="${DIR}/../lib"
EXCHANGIS_PID_PATH="${DIR}/../runtime"
# Default
MAIN_CLASS=""
DEBUG_MODE=False
DEBUG_PORT="7006"
SPRING_PROFILE="exchangis"
SLEEP_TIMEREVAL_S=2

function LOG(){
  currentTime=`date "+%Y-%m-%d %H:%M:%S.%3N"`
  echo -e "$currentTime [${1}] ($$) $2" | tee -a ${SHELL_LOG}
}

abs_path(){
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "${SOURCE}" ]; do
        DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
        SOURCE="$(readlink "${SOURCE}")"
        [[ ${SOURCE} != /* ]] && SOURCE="${DIR}/${SOURCE}"
    done
    echo "$( cd -P "$( dirname "${SOURCE}" )" && pwd )"
}

verify_java_env(){
    if [[ "x${JAVA_HOME}" != "x" ]]; then
      ${JAVA_HOME}/bin/java -version >/dev/null 2>&1
    else
      java -version >/dev/null 2>&1
    fi
    if [[ $? -ne 0 ]]; then
        cat 1>&2 <<EOF
+========================================================================+
| Error: Java Environment is not availiable, Please check your JAVA_HOME |
+------------------------------------------------------------------------+
EOF
        exit 1
    else
        return 0
    fi
}

# load environment definition
load_env_definitions(){
   if [[ -f "$1" ]]; then
      LOG INFO "load environment properties"
      while read line
      do
        if [[ ! -z $(echo "${line}" | grep "=") ]]; then
          local key=${line%%=*}
          local value=${line##*=}
          local key1=$(echo ${key} | tr '.' '_')
          if [[ -z $(echo "${key1}" | grep -P '\s*#+.*') ]]; then
            eval "${key1}=${value}"
          fi
        fi
      done < "${ENV_FILE}"
   fi
   if [[ "x${JAVA_HOME}" != "x" ]]; then
      JPS=${JAVA_HOME}/bin/jps
   else
      EXE_JAVA="java "${JAVA_OPTS}" "${MAIN_CLASS}
      JPS="jps"
  fi
}

construct_java_command(){
    verify_java_env
    if [[ "x${EXCHANGIS_CONF_PATH}" == "x" ]]; then
        LOG ERROR "Prop:EXCHANGIS_CONF_PATH is missing, must be not empty or blank"
        exit 1
    fi
    if [[ "x${EXCHANGIS_LIB_PATH}" == "x" ]]; then
        LOG ERROR "Prop:EXCHANGIS_LIB_PATH is missing, must be not empty or blank"
        exit 1
    fi
    if [[ "x${EXCHANGIS_LOG_PATH}" == "x" ]]; then
        LOG ERROR "Prop:EXCHANGIS_LOG_PATH is missing, must be not empty or blank"
        exit 1
    fi
    if [[ "x$2" == "x" ]]; then
        LOG ERROR "Prop:MAIN_CLASS is missing, must be not empty or blank"
        exit 1
    fi
    if [[ "x${HEAP_SIZE}" == "x" ]]; then
        HEAP_SIZE="2g"
    fi
    # mkdir
    mkdir -p ${EXCHANGIS_LOG_PATH}
    mkdir -p ${EXCHANGIS_PID_PATH}
    local classpath=${EXCHANGIS_CONF_PATH}":."
    local opts=""
    classpath=${EXCHANGIS_LIB_PATH}/"exchangis-server/*:"${classpath}
    LOG INFO "classpath:"${classpath}
    if [[ "x${EXCHANGIS_JAVA_OPTS}" == "x" ]]; then
      # Use G1 garbage collector
       local opts="-Xms${HEAP_SIZE} -Xmx${HEAP_SIZE} -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseG1GC -Xloggc:${EXCHANGIS_LOG_PATH}/$1-gc.log"
    fi
    if [[ "x${DEBUG_MODE}" == "xTrue" ]]; then
        if [[ "x${DEBUG_PORT}" == "x" ]]; then
            LOG ERROR  "Prop:DEBUG_PORT is missing, must be a number and not blank"
            exit 1
        fi
       opts=${opts}" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${DEBUG_PORT}"
    fi
    opts=${opts}" -XX:HeapDumpPath=${EXCHANGIS_LOG_PATH}/HeapDump/$1"
    if [[ "x"${SPRING_PROFILE} != "x" ]]; then
        opts=${opts}" -Dspring.profiles.active=${SPRING_PROFILE}"
    fi
    opts=${opts}" -DserviceName=$1 -Dlog.path=${EXCHANGIS_LOG_PATH} -Dpid.file=${EXCHANGIS_PID_PATH}/$1.pid"
    opts=${opts}" -Dlogging.level.reactor.ipc.netty.channel.CloseableContextHandler=off"
    opts=${opts}" -Duser.dir=${USER_DIR}"
    opts=${opts}" -classpath "${classpath}
    LOG INFO "opts:"${opts}
    if [[ "x${JAVA_HOME}" != "x" ]]; then
        EXEC_JAVA=${JAVA_HOME}"/bin/java "${opts}" "$2
    else
        EXEC_JAVA="java "${opts}" "$2
    fi
}

# check if the process still in jvm
status_class(){
    local p=""
    local pid_file_path=${EXCHANGIS_PID_PATH}/$1.pid
    if [ "x"${pid_file_path} != "x" ]; then
      if [ -f ${pid_file_path} ]; then
        local pid_in_file=`cat ${pid_file_path} 2>/dev/null`
        if [ "x"${pid_in_file} !=  "x" ]; then
          p=`${JPS} -q | grep ${pid_in_file} | awk '{print $1}'`
        fi
      fi
    else
      p=`${JPS} -l | grep "$2" | awk '{print $1}'`
    fi
    if [ -n "$p" ]; then
        # echo "$1 ($2) is still running with pid $p"
        return 0
    else
        # echo "$1 ($2) does not appear in the java process table"
        return 1
    fi
}

wait_for_startup(){
    local now_s=`date '+%s'`
    local stop_s=$((${now_s} + $1))
    while [ ${now_s} -le ${stop_s} ];do
        status_class $2 $3
        if [ $? -eq 0 ]; then
            return 0
        fi
        sleep ${SLEEP_TIMEREVAL_S}
        now_s=`date '+%s'`
    done
    return 1
}

wait_for_stop(){
    local now_s=`date '+%s'`
    local stop_s=$((${now_s} + $1))
    while [ ${now_s} -le ${stop_s} ];do
        status_class $2 $3
        if [ $? -eq 1 ]; then
            return 0
        fi
        sleep ${SLEEP_TIMEREVAL_S}
        now_s=`date '+%s'`
    done
    return 1
}

# Input: $1:module_name, $2:main class
launcher_start(){
    LOG INFO "Launcher: launch to start server [ $1 ]"
    status_class $1 $2
    if [[ $? -eq 0 ]]; then
      LOG INFO "Launcher: [ $1 ] has been started in process"
      return 0
    fi
    construct_java_command $1 $2
    # Execute
    echo ${EXEC_JAVA}
    LOG INFO ${EXEC_JAVA}
    nohup ${EXEC_JAVA}  >/dev/null 2>&1 &
    LOG INFO "Launcher: waiting [ $1 ] to start complete ..."
    wait_for_startup 20 $1 $2
    if [[ $? -eq 0 ]]; then
        LOG INFO "Launcher: [ $1 ] start success"
        LOG INFO ${EXCHANGIS_CONF_PATH}
        APPLICATION_YML="${EXCHANGIS_CONF_PATH}/application-exchangis.yml"
        EUREKA_URL=`cat ${APPLICATION_YML} | grep Zone | sed -n '1p'`
        echo "${EUREKA_URL}"
        LOG INFO "Please check exchangis server in EUREKA_ADDRESS: ${EUREKA_URL#*:} "
    else
        LOG ERROR "Launcher: [ $1 ] start fail over 20 seconds, please retry it"
    fi
}

# Input: $1:module_name, $2:main class
launcher_stop(){
    LOG INFO "Launcher: stop the server [ $1 ]"
    local p=""
    local pid_file_path=${EXCHANGIS_PID_PATH}/$1.pid
    if [ "x"${pid_file_path} != "x" ]; then
      if [ -f ${pid_file_path} ]; then
        local pid_in_file=`cat ${pid_file_path} 2>/dev/null`
        if [ "x"${pid_in_file} !=  "x" ]; then
          p=`${JPS} -q | grep ${pid_in_file} | awk '{print $1}'`
        fi
      fi
    elif [[ "x"$2 != "x" ]]; then
      p=`${JPS} -l | grep "$2" | awk '{print $1}'`
    fi
    if [[ -z ${p} ]]; then
      LOG INFO "Launcher: [ $1 ] didn't start successfully, not found in the java process table"
      return 0
    fi
    case "`uname`" in
      CYCGWIN*) taskkill /PID "${p}" ;;
      *) kill -SIGTERM "${p}" ;;
    esac
    LOG INFO "Launcher: waiting [ $1 ] to stop complete ..."
    wait_for_stop 20 $1 $2
    if [[ $? -eq 0 ]]; then
      LOG INFO "Launcher: [ $1 ] stop success"
    else
      LOG ERROR "Launcher: [ $1 ] stop exceeded over 20s " >&2
      return 1
    fi
}
