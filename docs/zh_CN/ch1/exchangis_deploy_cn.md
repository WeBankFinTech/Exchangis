### 1.Exchangis环境准备
#### 1.1）基础软件安装
- MySQL (5.5+) 必选，对应客户端可以选装, Linux服务上若安装mysql的客户端可以通过部署脚本快速初始化数据库 [如何安装mysql](https://www.runoob.com/mysql/mysql-install.html)
- JDK (1.8.0_141) 必选[如何安装JDK](https://www.runoob.com/java/java-environment-setup.html)
- Maven (3.6.1+) 必选[如何安装MAVEN](https://m.runoob.com/maven/maven-setup.html)
- SQOOP (1.4.6) 必选，如果想要SQOOP做传输引擎，要安装SQOOP，SQOOP安装依赖Hive,Hadoop环境
- DSS1.0.1必选，确保安装部署环境下有DSS服务，以便进行APPCONN接入[如何安装DSS](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/DSS%E5%8D%95%E6%9C%BA%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3.md)
- Linkis1.1.0必选，请求的路由规则，执行引擎等均需要linkis[如何安装Linkis](https://linkis.apache.org/zh-CN/docs/latest/deployment/quick_deploy)
- SQOOP引擎 必选，用户执行数据同步任务。[如何安装sqoop引擎](https://linkis.apache.org/zh-CN/docs/latest/deployment/quick_deploy)
- Nginx
- Hadoop(2.7.2，Hadoop其他版本需自行编译Linkis)，单机部署Hadoop可参考：[Hadoop单机部署](https://linkis.apache.org/zh-CN/docs/latest/deployment/quick_deploy) ；分布式部署Hadoop可参考：[Hadoop分布式部署](https://linkis.apache.org/zh-CN/docs/latest/deployment/quick_deploy)。
- Hive(2.3.3，Hive其他版本需自行编译Linkis) [Hive快速安装](https://linkis.apache.org/zh-CN/docs/latest/deployment/quick_deploy)

#### 1.2）创建用户
1.2.1.部署用户与部署linkis的用户保持一致，例如：部署用户是hadoop账号

在部署机器上创建部署用户，用于安装
```
sudo useradd hadoop  
```
1.2.2.底层依赖及组件检查

执行相应的命令，查看当前环境是否支持相关依赖及组件
```
hdfs  version
hive --version
curl 127.0.0.1:3306 | grep LINKIS  #通过eureka地址查看是否部署LINKIS服务
curl 127.0.0.1:3306 | grep DSS     #通过eureka地址查看是否部署DSS服务
```

### 2.安装包准备
#### 2.3）下载二进制包
从Exchangis已发布的release中[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases)，下载最新的安装包
#### 2.4） 编译打包（二进制包跳过）
直接从Git上面获得源代码，在项目的根目录下执行如下命令
```
mvn clean install 
```
执行成功后将会在工程的${EXCHANGIS_HOME}/assembly-package/target目录下生成安装包
```
target/wedatasphere-exchangis-{VERSION}.tar.gz
```


#### 2.3）解压安装包
先解压安装包到安装目录，并对解压后的文件进行配置修改。
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

#### 2.4）依赖LINKIS及MYBATIS的基础配置修改
```
vi config/config.sh
```
```
LINKIS_GATEWAY_HOST=          #LINKIS_GATEWAY服务地址IP，用于查找linkis-mg-gateway服务
LINKIS_GATEWAY_PORT=          #LINKIS_GATEWAY服务地址端口，用于查找linkis-mg-gateway服务
DATASOURCE_TOKEN=             #用于请求校验数据源的token,该字段可在linkis表linkis_mg_gateway_auth_token的token_name字段获取
LINKIS_TOKEN=                 #用于请求校验linkis服务的token
```

#### 2.5）修改数据库配置
```
# 设置数据库的连接信息
# 包括IP地址、数据库名称、用户名、端口
MYSQL_HOST=
MYSQL_PORT=
MYSQL_USERNAME=
MYSQL_PASSWORD=
DATABASE=
```

### 3.安装和启动

#### 3.1）执行一键安装脚本
进入解压后的目录，找到sbin目录下面的install.sh文件，如果选择交互式的安装，则直接执行
```
./sbin/install.sh
```

#### 3.2）安装步骤
该脚本为交互式安装，开始执行install.sh脚本后，安装步骤依次分为以下几步：
1.	解压缩lib包

当出现该提醒时：Do you want to decompress this package: [exchangis-server_1.0.0-RC1.tar.gz]

输入y确认解压，就会将项目的实际jar包解压到项目的根目录文件下lib下。
2.	安装部署数据库

当出现该提醒时：Do you want to initalize database with sql: [${SQL_SOURCE_PATH}]?

输入y确认，就会开始部署数据库
3.	配置exchangis-server.properties中基本的配置参数

当出现该提醒时候：Do you want to initalize exchangis-server.properties?

输入y确认，就会开始配置exchangis-server.properties中的相关参数

#### 3.3）启动服务

进入到sbin文件夹下，一键启动所有服务

```
cd sbin
./daemon.sh start server
```
中途可能发生启动失败或者卡住，可以退出重复执行

使用以下命令执行脚本，可一键完成服务的停止和重启
```
./daemon.sh restart server
```
执行完成启动脚本后，会出现以下提示
![企业微信截图_16532930262583](https://user-images.githubusercontent.com/27387830/169773764-1c5ed6fb-35e9-48cb-bac8-6fa7f738368a.png)

#### 3.4）查看服务是否启动成功
可以在Eureka界面查看服务启动成功情况，查看方法：

使用http://${EUREKA_INSTALL_IP}:${EUREKA_PORT}, 在浏览器中打开，查看服务是否注册成功。

如果您没有在application-eureka.yml指定EUREKA_INSTALL_IP和EUREKA_INSTALL_IP，则HTTP地址为：http://127.0.0.1:20303

默认启动的服务名称如下：
```
exchangis-server
```

Exchangis1.0通过EUREKA查看启动的服务，其端口号在配置文件application-exchangis.yml。
可根据需要修改服务IP及端口号，配置文件为application-exchangis.yml
```
port: XXXX
defaultZone: http://127.0.0.1:3306/eureka/
```

#### 3.5）查看服务是否正常
您也可以通过exchangis用户手册来测试exchangis是否能正常运行任务[点击跳转用户手册](https://github.com/WeBankFinTech/Exchangis/releases)

#### 3.6）前端安装部署
web端是使用nginx作为静态资源服务器的，如果您想要通过前端界面访问Exchangis1.0，就要进行以下几步对前端进行安装配置操作：

3.6.1.	获取前端安装包
这里提供Exchangis1.0前端包，您可以自行下载使用：
[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases)

如果您需要自行对前端代码进行开发编译，也可以在Exchangis1.0项目中找到前端web模块，路径为${EXCHANGIS_PROJECT_HOME}/web。这里以Fes编译为例，使用Fes命令对前端模块进行打包，编译步骤如下：
```
cd ${EXCHANGIS_HOME}/web
npm i
npm run build
```
通过上面的编译步骤，即可在${EXCHANGIS_PROJECT_HOME}/web/路径下生成编译好的dist.zip包，既为我们需要使用的前端包。
获取到的前端包，您可以放在服务器上的任意位置，这里建议您与后端安装地址目录保持一致，在同一目录下放置并解压。

3.6.2.前端安装部署

exchangis的nginx配置文件默认是在/etc/nginx/conf.d/linkis.conf nginx的日志文件在 /var/log/nginx/access.log 和/var/log/nginx/error.log。为了正确找到前端资源，需要在服务器上配置nginx的conf文件，这里提供一个exchangis.conf示例配置，您可根据实际需要进行修改：
```

        server {
            listen       8090;# 访问端口 如果端口被占用，则需要修改
            server_name  localhost;
            #charset koi8-r;
            #access_log  /var/log/nginx/host.access.log  main;
            location /linkis/visualis {
            root   /appcom/Install/exchangis/web; # 静态文件目录，需要修改
            autoindex on;
            }
            location / {
            proxy_pass http://127.0.0.1:9020;#后端Linkis的地址，需要修改
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection upgrade;
            }

            location /api {
            proxy_pass http://127.0.0.1:9020; #后端Linkis的地址，需要修改
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header x_real_ipP $remote_addr;
            proxy_set_header remote_addr $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection upgrade;
            }

            #error_page  404              /404.html;
            # redirect server error pages to the static page /50x.html
            #
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
            root   /usr/share/nginx/html;
            }
        }
```

3.6.3.启动nginx及访问前端页面
配置完成之后，使用以下命令重新启动nginx:
```
nginx -s reload
```

访问域名 https://www.open.source/origin/exchangis/(此处为示例域名，请根据实际域名进行修改)，即可进入Exchangis主界面：
![image](https://user-images.githubusercontent.com/27387830/170417473-af0b4cbe-758e-4800-a58f-0972f83d87e6.png)
