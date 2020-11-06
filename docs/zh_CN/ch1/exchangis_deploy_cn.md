### 环境准备
#### 1）基础软件安装
- MySQL (5.5+) 必选，对应客户端可以选装, Linux服务上若安装mysql的客户端可以通过部署脚本快速初始化数据库
- JDK (1.8.0_141) 必选
- Maven (3.6.1+) 必选
- SQOOP (1.4.6) 可选，如果想要SQOOP做传输引擎，可以安装SQOOP，SQOOP安装依赖Hive,Hadoop环境，这里就不展开来讲
- Python (2.x) 可选，主要用于调度执行底层DataX的启动脚本，默认的方式是以Java子进程方式执行DataX，用户可以选择以Python方式来做自定义的改造

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
执行成功后将会在工程的build目录下生成安装包
```
build/wedatasphere-exchangis-{VERSION}.tar.gz
```

### 开始部署
#### 1）解压安装包
在选定的安装目录，解压安装包
```
tar -zxvf wedatasphere-exchangis-{VERSION}.tar.gz
```
#### 2）执行一键安装脚本
进入解压后的目录，找到bin目录下面的install.sh文件，如果选择交互式的安装，则直接执行
```
./bin/install.sh
```
在交互模式下，对各个模块的package压缩包的解压以及configure配置脚本的调用，都会请求用户确认。
如果不想使用交互模式，跳过确认过程，则执行以下命令安装
```
./bin/install.sh --force
```

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
如果服务上并没有安装mysql命令，则可以取用目录下/bin/exchangis-init.sql脚本去手动执行，完成后修改相关配置文件
```
vi ./modules/exchangis-service/conf/bootstrap.properties
```
```
#Database
#DB_HOST=
#DB_PORT=
#DB_USERNAME=
#DB_PASSWORD=
#DB_DATABASE=
```
按照具体情况配置对应的值即可。

#### 4）启动服务
一键启动所有服务
```
./bin/start-all.sh
```
中途可能发生部分模块启动失败或者卡住，可以退出重复执行，如果需要改变某一模块服务端口号，则：
```
vi ./modules/{module_name}/bin/env.properties
```
找到SERVER_PORT配置项，改变它的值即可。
当然也可以单一地启动某一模块服务：
```
./bin/start.sh -m {module_name}
```

#### 4）查看服务
服务使用Eureka做注册中心，默认的Eureka端口是8500(可变), 可以在Eureka界面http://{EUREKA_IP}:{EUREKA_PORT}上观察服务是否正常启动。
Exchangis的入口界面集成在Gateway中，Gateway的访问端口为9503（可变）
--

Tips: 脚本使用的都是bash指令集，如若使用sh调用脚本，可能会有未知的错误