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

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SHELL_LOG="${DIR}/console.out"   #console.out是什么文件？
export SQL_SOURCE_PATH="${DIR}/../db/exchangis_ddl.sql"
PACKAGE_DIR="${DIR}/../packages"
# Home Path
EXCHNGIS_HOME_PATH="${DIR}/../"

CONF_FILE_PATH="bin/configure.sh"
FORCE_INSTALL=false
SKIP_PACKAGE=false
USER=`whoami`
SUDO_USER=false

CONF_PATH=${DIR}/../config

usage(){
   printf "\033[1m Install project, run directly\n\033[0m"
}

function LOG(){
  currentTime=`date "+%Y-%m-%d %H:%M:%S.%3N"`
  echo -e "$currentTime [${1}] ($$) $2" | tee -a ${SHELL_LOG}  # tee -a 输出是追加到文件里面
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

BIN=`abs_path`


is_sudo_user(){
  sudo -v >/dev/null 2>&1  #因为 sudo 在第一次执行时或是在 N分钟内没有执行(N 预设为5)会问密码
  #这条命令的意思就是在后台执行这个程序,并将错误输出2重定向到标准输出1,然后将标准输出1全部放到/dev/null文件,也就是清空.
  #所以可以看出" >/dev/null 2>&1 "常用来避免shell命令或者程序等运行中有内容输出。
}

uncompress_packages(){
  LOG INFO "\033[1m package dir is: [${PACKAGE_DIR}]\033[0m"
  local list=`ls ${PACKAGE_DIR}`
  LOG INFO "\033[1m package list is: [${list}]\033[0m"
  for pack in ${list}
  do
    local uncompress=true
    if [ ${#PACKAGE_NAMES[@]} -gt 0 ]; then
      uncompress=false
      for server in ${PACKAGE_NAMES[@]}
      do
        if [ ${server} == ${pack%%.tar.gz*} ] || [ ${server} == ${pack%%.zip*} ]; then
          uncompress=true
          break
        fi
      done
    fi
    if [ ${uncompress} == true ]; then
      if [[ ${pack} =~ tar\.gz$ ]]; then
        local do_uncompress=0
        if [ ${FORCE_INSTALL} == false ]; then
          interact_echo "Do you want to decompress this package: [${pack}]?"
          do_uncompress=$?
        fi
        if [ ${do_uncompress} == 0 ]; then
          LOG INFO "\033[1m Uncompress package: [${pack}] to modules directory\033[0m"
          tar --skip-old-files -zxf ${PACKAGE_DIR}/${pack} -C ../
        fi
      elif [[ ${pack} =~ zip$ ]]; then
        local do_uncompress=0
        if [ ${FORCE_INSTALL} == false ]; then
          interact_echo "Do you want to decompress this package: [${pack}]?"
          do_uncompress=$?
        fi
        if [ ${do_uncompress} == 0 ]; then
          LOG INFO "\033[1m Uncompress package: [${pack}] to modules directory\033[0m"
          unzip -nq ${PACKAGE_DIR}/${pack} -d   #  n 解压缩时不要覆盖原有的文件
        fi
      fi
      # skip other packages
    fi
  done
}

interact_echo(){
  while [ 1 ]; do
    read -p "$1 (Y/N)" yn
    if [ "${yn}x" == "Yx" ] || [ "${yn}x" == "yx" ]; then
      return 0
    elif [ "${yn}x" == "Nx" ] || [ "${yn}x" == "nx" ]; then
      return 1
    else
      echo "Unknown choise: [$yn], please choose again."
    fi
  done
}

init_database(){
BOOTSTRAP_PROP_FILE="${CONF_PATH}/exchangis-server.properties"
# Start to initalize database
if [ "x${SQL_SOURCE_PATH}" != "x" ] && [ -f "${SQL_SOURCE_PATH}" ]; then
   `mysql --version >/dev/null 2>&1`
   if [ $? == 0 ]; then
      LOG INFO "\033[1m Scan out mysql command, so begin to initalize the database\033[0m"
      interact_echo "Do you want to initalize database with sql: [${SQL_SOURCE_PATH}]?"
      if [ $? == 0 ]; then
        read -p "Please input the db host(default: 127.0.0.1): " HOST
        if [ "x${HOST}" == "x" ]; then
          HOST="127.0.0.1"
        fi
        while [ 1 ]; do
          read -p "Please input the db port(default: 3306): " PORT
          if [ "x${PORT}" == "x" ]; then
            PORT=3306
            break
          elif [ ${PORT} -gt 0 ] 2>/dev/null; then
            break
          else
            echo "${PORT} is not a number, please input again"
          fi
        done
        read -p "Please input the db username(default: root): " USERNAME
        if [ "x${USERNAME}" == "x" ]; then
          USERNAME="root"
        fi
        read -p "Please input the db password(default: ""): " PASSWORD
        read -p "Please input the db name(default: exchangis)" DATABASE
        if [ "x${DATABASE}" == "x" ]; then
          DATABASE="exchangis"
        fi
        DATASOURCE_URL="jdbc:mysql:\/\/${HOST}:${PORT}\/${DATABASE}\?useSSL=false\&characterEncoding=UTF-8\&allowMultiQueries=true"
        mysql -h ${HOST} -P ${PORT} -u ${USERNAME} -p${PASSWORD}  --default-character-set=utf8 -e \
        "CREATE DATABASE IF NOT EXISTS ${DATABASE}; USE ${DATABASE}; source ${SQL_SOURCE_PATH};"
        #sed -ri "s![#]?(DB_HOST=)\S*!\1${HOST}!g" ${BOOTSTRAP_PROP_FILE}
        #sed -ri "s![#]?(DB_PORT=)\S*!\1${PORT}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.linkis.server.mybatis.datasource.username=)\S*!\1${USERNAME}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.linkis.server.mybatis.datasource.password=)\S*!\1${PASSWORD}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.linkis.server.mybatis.datasource.url=)\S*!\1${DATASOURCE_URL}!g" ${BOOTSTRAP_PROP_FILE}
      fi
   fi
fi
}

init_properties(){
BOOTSTRAP_PROP_FILE="${CONF_PATH}/exchangis-server.properties"
# Start to initalize propertis
      interact_echo "Do you want to initalize exchangis-server.properties?"
      if [ $? == 0 ]; then
        read -p "Please input the linkis gateway ip(default: 127.0.0.1): " HOST
        if [ "x${HOST}" == "x" ]; then
          HOST="127.0.0.1"
        fi
        while [ 1 ]; do
          read -p "Please input the linkis gateway port(default: 3306): " PORT
          if [ "x${PORT}" == "x" ]; then
            PORT=3306
            break
          elif [ ${PORT} -gt 0 ] 2>/dev/null; then
            break
          else
            echo "${PORT} is not a number, please input again"
          fi
        done

        LINKIS_GATEWAY_URL="http:\/\/${HOST}:${PORT}\/"

        read -p "Please input the exchangis datasource client serverurl(default: http://127.0.0.1:3306): " EXCHANGIS_DATASOURCE_URL
        if [ "x${EXCHANGIS_DATASOURCE_URL}" == "x" ]; then
          EXCHANGIS_DATASOURCE_URL="http://127.0.0.1:3306"
        fi
        read -p "Please input the linkis server url(default: ""): " LINKIS_SERVER_URL
        if [ "x${LINKIS_SERVER_URL}" == "x" ]; then
          LINKIS_SERVER_URL="http://127.0.0.1:3306"
        fi

        sed -ri "s![#]?(wds.linkis.gateway.ip=)\S*!\1${HOST}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.linkis.gateway.port=)\S*!\1${PORT}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.linkis.gateway.url=)\S*!\1${LINKIS_GATEWAY_URL}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.exchangis.datasource.client.serverurl=)\S*!\1${EXCHANGIS_DATASOURCE_URL}!g" ${BOOTSTRAP_PROP_FILE}
        sed -ri "s![#]?(wds.exchangis.client.linkis.server-url=)\S*!\1${LINKIS_SERVER_URL}!g" ${BOOTSTRAP_PROP_FILE}
      fi
}

install_modules(){
  LOG INFO "\033[1m ####### Start To Install project ######\033[0m"
  echo ""
    if [ ${FORCE_INSTALL} == false ]; then
      interact_echo "Do you want to confiugre and install project?"
      if [ $? == 0 ]; then  #$? 执行上一个指令的返回值 (显示最后命令的退出状态。0表示没有错误，其他任何值表明有错误)
        LOG INFO "\033[1m Install project ......\033[0m"
        init_database
        init_properties
      fi
    else
      LOG INFO "\033[1m Install project ......\033[0m"
      init_database
    fi
  LOG INFO "\033[1m ####### Finish To Install Project ######\033[0m"
}


while [ 1 ]; do
  case ${!OPTIND} in
  -h|--help)
    usage
    exit 0
  ;;
  "")
    break
  ;;
  *)
    echo "Argument error! " 1>&2
    exit 1
  ;;
  esac
done

is_sudo_user
if [ $? == 0 ]; then
  SUDO_USER=true
fi

MODULE_LIST_RESOLVED=()
c=0
RESOLVED_DIR=${PACKAGE_DIR}

server="exchangis-server"
LOG INFO  "\033[1m ####### server is [${server}] ######\033[0m"
server_list=`ls ${RESOLVED_DIR} | grep -E "^(${server}|${server}_[0-9]+\\.[0-9]+\\.[0-9]+)" | grep -E "(\\.tar\\.gz|\\.zip|)$"`
LOG INFO  "\033[1m ####### server_list is [${server_list}] ######\033[0m"
for _server in ${server_list}
    do
      # More better method to cut string?
      _server=${_server%%.tar.gz*}
      _server=${_server%%zip*}
      MODULE_LIST_RESOLVED[$c]=${_server}
      c=$(($c + 1))
    done
if [ ${SKIP_PACKAGE} == true ]; then
    MODULE_LIST=${MODULE_LIST_RESOLVED}
else
    PACKAGE_NAMES=${MODULE_LIST_RESOLVED}
fi


LOG INFO  "\033[1m ####### Start To Uncompress Packages ######\033[0m"
LOG INFO  "Uncompressing...."
uncompress_packages
LOG INFO  "\033[1m ####### Finish To Umcompress Packages ######\033[0m"

 install_modules


exit 0

