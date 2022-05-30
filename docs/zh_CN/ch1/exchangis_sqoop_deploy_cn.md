### 环境准备
Sqoop引擎是执行Exchangis数据同步任务不可或缺的组件，只有安装部署完成Sqoop引擎才能够成功执行数据同步任务。同时，确保所部署的机器上有安装sqoop，本文也会对sqoop的安装进行讲解。

您在安装部署Sqoop引擎之前，请按照Exchangis1.0.0安装部署文档安装完成Exchangis1.0.0及相关组件的安装，并确保工程基本功能可用。

### 安装包准备
#### 1）下载二进制包
[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases)
#### 2） 编译打包
如果您想自己开发和编译sqoop引擎，具体编译步骤如下：

1.clone Exchangis的代码

2.在exchangis-plugins模块下，找到sqoop引擎，单独编译sqoop，操作如下
```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/engine/sqoop
mvn clean install
```
然后会在该路径下找到sqoop引擎安装包
```
{EXCHANGIS_CODE_HOME}\exchangis-plugins\sqoop\target\out\sqoop
```


### 开始部署
#### 1）sqoop引擎安装
1.拿到打包出来的sqoop.zip物料包

2.放置到如下目录并进行解压
```
cd {LINKIS_HOME}/linkis/lib/linkis-engineconn-plugins
unzip.zip
```
解压出来的目录结构为：
```
dist
plugin
```
(注意，看当前sqoop引擎对哪些用户有权限，不一定是root)


#### 2）重启linkis-engineplugin服务使sqoop引擎生效
新加入linkis的引擎都要重启linkis的engineplugin服务才会生效，重启脚本为linkis安装目录下的./linkis-daemon.sh，具体步骤如下
```
{LINKIS _INSTALL_HOME}/links/sbin/
./linkis-daemon.sh restart cg-engineplugin
```
待服务启动成功，至此，sqoop安装部署就完成了。
