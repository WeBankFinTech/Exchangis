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


MODULE_NAME=""
usage(){
  echo "Usage is [-m module will be started]"
}

if [[ "x"${EXCHANGIS_LIB_PATH} == "x" ]]; then
  MODULE_LIB=${EXCHANGIS_LIB_PATH}
else
  MODULE_LIB=${BIN}/../lib
fi

start_single_module(){
  local main_class=${MODULE_MAIN_CLASS[$1]}
  if [[ "x"${main_class} == "x" ]]; then
      LOG ERROR "Cannot find main class for module [ $1 ]"
      exit 1
  fi
  if [[ ! -d ${MODULE_LIB}/$1 ]]; then
     LOG ERROR "Cannot find the lib directory for module [ $1 ]"
     exit 1
  fi
  # call launcher
  launcher_start $1 ${main_class}
}

while [ 1 ]; do
  case ${!OPTIND} in
  -m|--modules)
    if [ -z $2 ]; then
      LOG ERROR "No module provided"
      exit 1
    fi
    MODULE_NAME=$2
    shift 2
  ;;
  "")
    break
  ;;
  *)
    usage
    exit 1
  ;;
  esac
done

if [ "x${MODULE_NAME}" == "x" ]; then
  usage
  exit 1
fi

start_single_module ${MODULE_DEFAULT_PREFIX}${MODULE_NAME}
exit $?
