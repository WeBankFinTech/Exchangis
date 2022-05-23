### 环境准备
#### 1）基础软件安装
- MySQL (5.5+) 必选，对应客户端可以选装, Linux服务上若安装mysql的客户端可以通过部署脚本快速初始化数据库
- JDK (1.8.0_141) 必选
- Maven (3.6.1+) 必选
- SQOOP (1.4.6) 必选，如果想要SQOOP做传输引擎，可以安装SQOOP，SQOOP安装依赖Hive,Hadoop环境，这里就不展开来讲
- DSS1.0.1必选，确保安装部署环境下有DSS服务，以便进行APPCONN接入
- Linkis1.1.0必选，请求的路由规则，执行引擎等均需要linkis

#### 2）选择用户
如果选择有sudo权限的用户来执行安装部署脚本，并启动服务，对于不同的数据交换作业，服务将会切换用户来执行，否则将以当前服务所属用户来执行。

### 安装包准备
#### 1）下载二进制包
[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases)
#### 2） 编译打包（二进制包跳过）
直接从Git上面获得源代码，在项目的根目录下执行如下命令
```
mvn clean install 
```
执行成功后将会在工程的assembly-package/target目录下生成安装包
```
target/wedatasphere-exchangis-{VERSION}.tar.gz
```

### 开始部署
#### 1）解压安装包
在选定的安装目录，解压安装包
```
tar -zxvf wedatasphere-exchangis-{VERSION}.tar.gz
```
在解压出来的目录结构为：
```
config

db

exchangis-extds

packages

sbin
```
其中，config为项目相关配置文件，db为数据库表sql文件夹，sbin为各种自动化脚本存放的文件夹。
#### 2）执行一键安装脚本
进入解压后的目录，找到sbin目录下面的install.sh文件，如果选择交互式的安装，则直接执行
```
./sbin/install.sh
```
该脚本为交互式安装，安装步骤依次分为以下几步：
1.	解压缩lib包
当出现该提醒时：Do you want to decompress this package: [exchangis-server_1.0.0-RC1.tar.gz]
输入y确认解压，就会将项目的实际jar包解压到项目的根目录文件下lib下。
2.	安装部署数据库
3.	配置exchangis-server.properties中基本的配置参数


#### 3）数据库初始化
如果你的服务上安装有mysql命令，在执行安装脚本的过程中则会出现以下提醒：
```
Scan out mysql command, so begin to initalize the database
Do you want to initalize database with sql: [{INSTALL_PATH}/bin/exchangis-init.sql]? (Y/N)y
Please input the db host(default: 127.0.0.1): 
Please input the db port(default: 3306): 
Please input the db username(default: root): 
Please input the db password(default: ): 
Please input the db name(default: exchangis)
```
按照提示输入数据库地址，端口号，用户名，密码以及数据库名称，大部分情况下即可快速完成初始化。
同时，会自动配置exchangis-server.properties中的下列参数：
```
#wds.linkis.server.mybatis.datasource.url= jdbc:mysql://localhost:3306/database?useSSL=false&characterEncoding=UTF-8&allowMultiQueries=true
#wds.linkis.server.mybatis.datasource.username=
#wds.linkis.server.mybatis.datasource.password=
```
如果服务上并没有安装mysql命令，则可以取用目录下/db/exchangis-ddl.sql脚本去手动执行，完成后修改exchangis-server.properties相关数据库配置参数。
注意，初始化数据库可能碰到的问题有：数据库访问权限不够，已存在同名数据库，防火墙未关闭等，视具体情况解决。

也可选择手动安装数据库，数据库表ddl和dml在db文件夹中，分别为exchangis_ddl.sql和exchangis_dml.sql。执行上述两个sql文件即刻完成库表创建。

#### 4）配置exchangis-server.properties中基本的配置参数
在执行脚本过初中，出现以下提示，既说明需要配置除数据库参数外其他必须参数：
```
Do you want to initalize exchangis-server.properties? (Y/N)y
Please input the linkis gateway ip(default: 127.0.0.1):（linkis gateway服务ip，必配）
Please input the linkis gateway port(default: 3306): （linkis gateway端口，必配）
Please input the exchangis datasource client serverurl(default: http://127.0.0.1:3306):（数据源服务url，用于执行数据同步任务，必配）
Please input the linkis server url(default: ""): （linkis服务url，必配）
```
以上参数均可自行在exchangis-server.properties文件中自行配置

#### 4）启动服务
一键启动所有服务
```
./sbin/start.sh或者./sbin/daemon.sh start
```
中途可能发生启动失败或者卡住，可以退出重复执行

使用以下命令执行脚本，可一键完成服务的停止和重启
```
./sbin/daemon.sh restart server
```
出现以下提示，说明exchangis服务启动成功
![企业微信截图_16532930262583](https://user-images.githubusercontent.com/27387830/169773764-1c5ed6fb-35e9-48cb-bac8-6fa7f738368a.png)

```
#### 5）查看服务
Exchangis1.0通过EUREKA查看启动的服务，其端口号在配置文件application-exchangis.yml。通过服务端口在网页上查看。
可根据需要修改服务IP及端口号，配置文件为application-exchangis.yml
```
port: XXXX
defaultZone: http://127.0.0.1:3306/eureka/
```
