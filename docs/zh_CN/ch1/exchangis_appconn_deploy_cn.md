### 环境准备
您在部署ExchangisAppConn之前，请按照[Exchangis1.0.0安装部署文档](https://github.com/WeDataSphere/Exchangis/blob/dev-1.0.0-rc/docs/zh_CN/ch1/exchangis_deploy_cn.md)安装完成Exchangis1.0.0及其他相关组件的安装，并确保工程基本功能可用。

### 安装包准备
#### 1）下载二进制包
[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases)
#### 2） 编译打包
如果您想自己开发和编译ExchangisAppConn，具体编译步骤如下：
1.clone Exchangis的代码
2.在exchangis-plugins模块下，找到exchangis-appconn，单独编译exchangis-appconn
```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn
mvn clean install
```
会在该路径下找到exchangis-appconn.zip安装包
```
{EXCHANGIS_CODE_HOME}\exchangis-plugins\exchangis-appconn\target\exchangis-appconn.zip
```

### 开始部署
#### 1）ExchangisAppConn插件的部署和配置总体步骤
1.拿到打包出来的exchangis-appconn.zip物料包

2.放置到如下目录并进行解压
```
cd {DSS_HOME}/dss/dss-appconns
unzip visualis-appconn.zip
```
解压出来的目录结构为：
```
conf
db
lib
appconn.properties
```
3.配置ExchangisAppConn数据库表的相关信息

4.执行脚本进行自动化安装
```
cd {EXCHANGIS_INSTALL_HOME}/sbin
./install-appconn.sh
```
该脚本为交互式的安装方案，您需要输入连接DSS数据库ip等相关信息，完成ExchangisApponn在DSS中的安装。我们将在接下来的章节中着重说明。

5.完成exchangis-appconn的安装后，需要重启dss服务，才能最终完成插件的更新

#### 2）Init.sql配置ExchangisAppConn数据库表的相关信息(重要)
想要使exchangis-appconn正确部署成功，最重要的是将init.sql中相关的字段值配置正确，错误的配置方式有可能导致某些appconn功能无法生效甚至dss无法启动。在这里，为了让您尽快部署一个可用的exchangis-appconn，我们已经提供给您一个配置较为完整的init.sql文件，但仍需您根据自身需要配置以下几个参数，除此之外，如果您需要更进一步的了解dss-appconn的架构和配置，可以参考dss文档进行学习：

dss_application表：url字段修改为您的前端域名或IP加端口的形式，例如https://www.exchangis.com/或http://127.0.0.1:3306/。然后，project_url、homepage_url、redirect_url这三个字段值正常为exchangis的首页地址，三个字段值相同即可，例如https://www.exchangis.com/#/projectManage或http://127.0.0.1:3306/#/projectManage

(注：只改域名部分或IP和端口部分即可！！)

sql语句如下（在init.sql文件中）：
![image](https://user-images.githubusercontent.com/27387830/169785874-a9b87fd4-6846-4186-acd3-f3db91f64d79.png)


dss_appconn_instance表：url字段修改为您exchangis后端的IP加端口的形式，例如http://127.0.0.1:3306/。注意，这里不能用域名，否则APPCONN插件无法调用到exchangis后端接口。homepage_url为主页url，例如https://www.exchangis.com/#/projectManage或http://127.0.0.1:3306/#/projectManage。最后redirect_url直接填写域名或IP加端口，例如https://www.exchangis.com/或http://127.0.0.1:3306/

sql语句如下（在init.sql文件中）：
![image](https://user-images.githubusercontent.com/27387830/169786001-5151adac-7b2c-498a-bec4-f0b7f6e8a1e9.png)

dss_workflow_node表：该表为dss工作流界面第三方节点的配置表，简单理解为配置了该表既会在dss工作流界面显示一个新的工作流节点。主要需要修改的只有jump_url字段，该字段用于配置双击sqoop节点时，跳转的前端界面url。字段值如下形式
http://127.0.0.1:3306/#/childJobManage。只修改IP和端口部分即可

sql语句如下（在init.sql文件中）：
![image](https://user-images.githubusercontent.com/27387830/169786065-1391cf24-f88c-4f47-9a26-3d810fbafa22.png)

#### 3）执行脚本进行自动化安装
进入DSS安装目录下，将exchangis.zip文件(APPCONN安装包)放入{DSS_INSTALL_HOME}/dss-appconns目录下并解压。

在解压出来的{DSS_INSTALL_HOME}/dss-appconns/exchangis/db目录下找到安装部署脚本appconn-install.sh，并使用如下命令执行安装部署脚本

```
sh appconn-install.sh
```

该脚本为交互式安装，安装步骤依次分为以下几步：
1.	判断已安装好的exchangis是以域名访问还是ip加port形式访问。

当出现该提醒时：Whether your exchangis appconn is accessed with a domain name？(Y/N)

输入Y，说明您的exchangis使用域名进行访问，需要按照第二章第五点在init.sql中提前配置相应参数，再执行脚本接下来的数据库初始化步骤。否则为使用ip和端口的形式访问exchangis，为脚本交互式配置参数。

输入N，会出现以下提示

```
Please enter the ip of appconn: （输入访问excahngis的IP）
Please enter the port of appconn: （输入访问exchangis的端口）
```

2.	exchangis-appconn数据库初始化。


#### 4）exchangis-appconn数据库初始化
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

如果服务上并没有安装mysql命令，则可以取用目录下/db/init.sql脚本去手动执行。（需提前知道您的DSS数据库地址）

注意，初始化数据库可能碰到的问题有：数据库访问权限不够，已存在同名数据库，防火墙未关闭等，视具体情况解决。


#### 5）使部署好的APPCONN生效
使用DSS启停脚本使APPCONN生效，脚本所在位置为{DSS_INSTALL_HOME}/sbin中，依次使用如下命令执行脚本
```
sh /sbin/dss-stop-all.sh
sh /sbin/dss-start-all.sh
```
中途可能发生启动失败或者卡住，可以退出重复执行

#### 5）验证exchangis-appconn是否生效
在安装部署完成exchangis-appconn之后，可通过以下步骤初步验证exchangis-appconn是否安装成功。
1.	在DSS工作空间创建一个新的项目
![image](https://user-images.githubusercontent.com/27387830/169782142-b2fc2633-e605-4553-9433-67756135a6f1.png)

2.	在exchangis端查看是否同步创建项目，创建成功说明appconn安装成功
![image](https://user-images.githubusercontent.com/27387830/169782337-678f2df0-080a-495a-b59f-a98c5a427cf8.png)



