#!/bin/sh
#Actively load user env
source ~/.bashrc
shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

SOURCE_ROOT=${workDir}

APPCONN_NAME='exchangis'
APPCONN_INSTALL_IP=127.0.0.1
APPCONN_INSTALL_PORT=8088

#echo "Current path of init sql is ${DB_DML_PATH}"
LOCAL_IP="`ifconfig | grep 'inet' | grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $2}'`"  #cut -d: -f2  按照:号进行分割，筛选出第二列的数据

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " + $1
      exit 1
  else
      echo "Succeed to" + $1
  fi
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

#PROC_NAME=DSSProjectServerApplication
#ProcNumber=`ps -ef |grep -w $PROC_NAME|grep -v grep|wc -l`
#if [ $ProcNumber -le 0 ];then
#   echo "${PROC_NAME} is not running,Please check whether DSS is installed"
#   exit 1000
#else
#   echo "Begine to install appconn"
#fi

##choose install mysql mode
function initInstallAppConn() {
  echo "Current installation component is exchangis"

  echo ""
  echo "If this machine(127.0.0.1) is installed, enter 1"
  echo "For others, you need to enter a complete IP address."
  read -p "Please enter the ip of appconn: "  ip
  APPCONN_INSTALL_IP=$ip
  if [[ '1' = "$ip" ]];then
   APPCONN_INSTALL_IP="127.0.0.1"
  fi
  echo "You input ip is ${APPCONN_INSTALL_IP}"

  echo ""
  read -p "Please enter the port of appconn:"  port
  APPCONN_INSTALL_PORT=$port
  echo "You input ip is ${APPCONN_INSTALL_PORT}"
}

function replaceCommonIp() {
 if [[ $APPCONN_INSTALL_IP == "127.0.0.1" ]] || [[ $APPCONN_INSTALL_IP == "0.0.0.0" ]];then
    echo "APPCONN_INSTALL_IP is equals $APPCONN_INSTALL_IP, we will change it to ip address"
    APPCONN_INSTALL_IP=$LOCAL_IP
 fi
}

##choose execute mysql mode
function executeSQL() {
  TEMP_DB_DML_PATH=${shellDir}
  DB_DML_PATH=$TEMP_DB_DML_PATH/init_real.sql
  cp -rf $TEMP_DB_DML_PATH/init.sql $DB_DML_PATH

  if [ $1 == "y" ] then
    break
  else
    sed -i "s/APPCONN_INSTALL_IP/$APPCONN_INSTALL_IP/g"       $DB_DML_PATH
    sed -i "s/APPCONN_INSTALL_PORT/$APPCONN_INSTALL_PORT/g"   $DB_DML_PATH
    sed -i "s#DSS_INSTALL_HOME_VAL#$DSS_INSTALL_HOME#g" $DB_DML_PATH
  fi
  read -p "Please input the db host(default: 127.0.0.1): " MYSQL_HOST
        if [ "x${MYSQL_HOST}" == "x" ]; then
          MYSQL_HOST="127.0.0.1"
        fi
        while [ 1 ]; do
          read -p "Please input the db port(default: 3306): " $MYSQL_PORT
          if [ "x${$MYSQL_PORT}" == "x" ]; then
            $MYSQL_PORT=3306
            break
          elif [ ${$MYSQL_PORT} -gt 0 ] 2>/dev/null; then
            break
          else
            echo "${$MYSQL_PORT} is not a number, please input again"
          fi
        done
        read -p "Please input the db username(default: root): " MYSQL_USER
        if [ "x${MYSQL_USER}" == "x" ]; then
          MYSQL_USER="root"
        fi
        read -p "Please input the db password(default: ""): " MYSQL_PASSWORD
        read -p "Please input the db name(default: exchangis)" MYSQL_DB
        if [ "x${MYSQL_DB}" == "x" ]; then
          MYSQL_DB="exchangis"
        fi
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"
  isSuccess "source $DB_DML_PATH"
  echo "the table update finished"
}

echo ""
echo "step1:Start to install exchangis appconn. Initialize installation settings"
useDomain=""
interact_echo "Whether your exchangis appconn is accessed with a domain name?"
if [ $? == 0 ]; then
  echo "Please configure the relevant domain name parameters in init.sql in advance"
  useDomain="y"
else
  initInstallAppConn
fi
echo ""

echo "step2:replaceIp"
if [ ${useDomain} != "y"]; then
  replaceCommonIp
fi
echo ""

echo "step3:update database"
executeSQL ${useDomain}
echo ""

echo ""
echo "step4:Restart the DSS service,please use sbin/dss-start-all.sh to restart it!"
echo ""