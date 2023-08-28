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

if [[ "x"${EXCHANGIS_HOME} != "x" ]]; then
  source ${EXCHANGIS_HOME}/sbin/launcher.sh
  source ${EXCHANGIS_HOME}/sbin/common.sh
else
  source ./launcher.sh
  source ./common.sh
fi

usage(){
  echo "Usage is [start|stop|restart {server}]"
}

start(){
  # call launcher
  launcher_start $1 $2
}

stop(){
  # call launcher
  launcher_stop $1 $2
}

restart(){
  launcher_stop $1 $2
  if [[ $? -eq 0 ]]; then
    sleep 3
    launcher_start $1 $2
  fi
}

COMMAND=$1
case $COMMAND in
  start|stop|restart)
    load_env_definitions ${ENV_FILE}
    if [[ ! -z $2 ]]; then
      SERVICE_NAME=${MODULE_DEFAULT_PREFIX}$2${MODULE_DEFAULT_SUFFIX}
      MAIN_CLASS=${MODULE_MAIN_CLASS[${SERVICE_NAME}]}
      if [[ "x"${MAIN_CLASS} != "x" ]]; then
        $COMMAND ${SERVICE_NAME} ${MAIN_CLASS}
      else
        LOG ERROR "Cannot find the main class for [ ${SERVICE_NAME} ]"
      fi
    else
      usage
      exit 1
    fi
    ;;
  *)
    usage
    exit 1
    ;;
esac