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
SHELL_LOG="${DIR}/launcher.out"
USER_DIR="${DIR}/../"
EXCHANGIS_CONF_PATH="${DIR}/../config"
EXCHANGIS_LIB_PATH="${DIR}/../lib"
EXCHANGIS_LOG_PATH=""
COMMON_CONF_FILE_NAME="exchangis.properties"
MODULE_CONF_FILE_PATTERN="exchangis-{module}.properties"
# Default
MAIN_CLASS=""
DEBUG_MODE=False
DEBUG_PORT="7006"
SPRING_PROFILE="exchangis"
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
    if [[ "x${MAIN_CLASS}" == "x" ]]; then
        LOG ERROR "Prop:MAIN_CLASS is missing, must be not empty or blank"
        exit 1
    fi
    if [[ "x${HEAP_SIZE}" == "x" ]]; then
        HEAP_SIZE="2g"
    fi
    local classpath=${EXCHANGIS_CONF_PATH}":."
    local opts=""
    if [[ "x"$2 != "x" ]]; then
        classpath=${EXCHANGIS_LIB_PATH}/$2":"${classpath}
    else
        classpath=${EXCHANGIS_LIB_PATH}/$1":"${classpath}
    fi
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
    opts=${opts}" -XX:HeapDumpPath=${EXCHANGIS_LOG_PATH}"
    if [[ "x"${SPRING_PROFILE} != "x" ]]; then
        opts=${opts}" -Dspring.profiles.active=${SPRING_PROFILE}"
    fi
    opts=${opts}" -Dlogging.level.reactor.ipc.netty.channel.CloseableContextHandler=off"
    opts=${opts}" -Duser.dir=${USER_DIR}"
    opts=${opts}" -classpath "${classpath}
    if [[ "x${JAVA_HOME}" != "x" ]]; then
        EXE_JAVA=${JAVA_HOME}"/bin/java "${opts}" "${MAIN_CLASS}
    else
        EXE_JAVA="java "${opts}" "${MAIN_CLASS}
    fi
}

# Input: $1:module_name, $2:module_tab
launcher_start(){
    load_env_definitions ${ENV_FILE}
    construct_java_command $1 $2
    # Execute
    LOG INFO ${EXEC_JAVA}
    nohup ${EXEC_JAVA}  >/dev/null 2>&1 &
    wait_for_startup
    if [[ $? -eq 0 ]]; then
        LOG INFO "Launcher:  $1 start success"
    else
        LOG INFO ""
    fi
}

launcher_stop(){
    load_env_definitions ${ENV_FILE}
}