Exchangis 升级文档
本文主要介绍在原有安装Exchangis服务的基础上适配DSS1.1.0和Linkis1.1.1的升级步骤，Exchangis1.0.0相对与Exchangis1.0.0-rc1版本最大的区别在于ExchangisAppconn的安装，需要对整个Exchangisappconn进行重新替换和加载
### 1.升级Exchangis前的工作
您在升级Exchangis之前，请按照[DSS1.1.0安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.1-rc/docs/zh_CN/ch1/exchangis_deploy_cn.md)
和[Linkis1.1.1安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.1-rc/docs/zh_CN/ch1/exchangis_deploy_cn.md)完成DSS和Linkis的安装升级

### 2.Exchangis升级步骤

#### 1）删除旧版本ExchangisAppconn包

进入下列目录，找到exchangis的appconn文件夹并删除，如果存在的话：
```
{DSS_Install_HOME}/dss/dss-appconns
```

#### 2）下载二进制包
我们提供ExchangisAppconn的升级物料包，您可直接下载使用。[点击跳转 Release 界面](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/WeDatasphere/Exchangis/exchangis1.0.0-rc/exchangis-appconn.zip)

#### 3） 编译打包

如果您想自行编译ExchangisAppConn，具体编译步骤如下：
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

### 3.ExchangisAppConn插件的部署和配置总体步骤
1.拿到打包出来的exchangis-appconn.zip物料包

2.放置到如下目录并进行解压

```
cd {DSS_Install_HOME}/dss/dss-appconns
unzip exchangis-appconn.zip
```
解压出来的目录结构为：
```
conf
db
lib
appconn.properties
```

3.执行脚本进行自动化安装

```shell
cd {DSS_INSTALL_HOME}/dss/bin
./install-appconn.sh
# 脚本是交互式的安装方案，您需要输入字符串exchangis以及exchangis服务的ip和端口，即可以完成安装
# 这里的exchangis端口是指前端端口，在nginx进行配置。而不是后端的服务端口
```

### 4.完成exchangis-appconn的安装后，调用脚本刷新appconn服务

#### 4.1）使部署好的APPCONN生效
使用DSS刷新使APPCONN生效，进入到脚本所在目录{DSS_INSTALL_HOME}/bin中，使用如下命令执行脚本，注意，无需重启dss服务：
```
sh ./appconn-refresh.sh
```

#### 4.2）验证exchangis-appconn是否生效
在安装部署完成exchangis-appconn之后，可通过以下步骤初步验证exchangis-appconn是否安装成功。
1.	在DSS工作空间创建一个新的项目
![image](https://user-images.githubusercontent.com/27387830/169782142-b2fc2633-e605-4553-9433-67756135a6f1.png)

2.	在exchangis端查看是否同步创建项目，创建成功说明appconn安装成功
![image](https://user-images.githubusercontent.com/27387830/169782337-678f2df0-080a-495a-b59f-a98c5a427cf8.png)

#### 4.3）更新Exchangis安装目录下的lib包

将从下载连接中得到的exchangis-project-server-1.0.0.jar和exchangis-server-1.0.0.jar两个包放入以下Exchangis安装目录的文件路径下(先删除原有旧的这两个包)：

```$xslt
lib/exchangis-server
```

再通过以下命令完成 Exchangis Server 的更新重启：
   
   ```shell script
   ./sbin/daemon.sh restart server
   ```

 更多使用操作可参照[Exchangis1.0用户手册](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.1-rc/docs/zh_CN/ch1/exchangis_user_manual_cn.md)
