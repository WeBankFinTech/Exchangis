ExchangisAppConn安装文档
本文主要介绍在DSS(DataSphere Studio)1.0.1中ExchangisAppConn的部署、配置以及安装
### 1.部署ExchangisAppConn的准备工作
您在部署ExchangisAppConn之前，请按照[Exchangis1.0.0安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/zh_CN/ch1/exchangis_deploy_cn.md)安装完成Exchangis1.0.0及其他相关组件的安装，并确保工程基本功能可用。

### 2.ExchangisAppConn插件的下载和编译
#### 1）下载二进制包
我们提供ExchangisAppconn的物料包，您可直接下载使用。[点击跳转 Release 界面](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/WeDatasphere/Exchangis/exchangis1.0.0/exchangis-appconn.zip)
#### 2） 编译打包

如果您想自己开发和编译ExchangisAppConn，具体编译步骤如下：
1.clone Exchangis的代码
2.在exchangis-plugins模块下，找到exchangis-appconn，单独编译exchangis-appconn
```shell
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn
mvn clean install
```
会在该路径下找到exchangis-appconn.zip安装包
```shell
{EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn/target/exchangis-appconn.zip
```

### 3.ExchangisAppConn插件的部署和配置总体步骤
1.拿到打包出来的exchangis-appconn.zip物料包

2.放置到如下目录并进行解压

注意：第一次解压exchangis appconn后，确保当前文件夹下没有index_v0000XX.index文件，该文件在后面才会生成

```shell
cd {DSS_Install_HOME}/dss/dss-appconns
unzip exchangis-appconn.zip
```
解压出来的目录结构为：
```shell
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

### 4.完成exchangis-appconn的安装后，需要重启dss服务，才能最终完成插件的更新

#### 4.1）使部署好的APPCONN生效
使用DSS启停脚本使APPCONN生效，进入到脚本所在目录{DSS_INSTALL_HOME}/sbin中，依次使用如下命令执行脚本：
```shell
sh ./dss-stop-all.sh
sh ./dss-start-all.sh
```
#### 4.2）验证exchangis-appconn是否生效
在安装部署完成exchangis-appconn之后，可通过以下步骤初步验证exchangis-appconn是否安装成功。
1.	在DSS工作空间创建一个新的项目
![image](https://user-images.githubusercontent.com/27387830/169782142-b2fc2633-e605-4553-9433-67756135a6f1.png)

2.	在exchangis端查看是否同步创建项目，创建成功说明appconn安装成功
![image](https://user-images.githubusercontent.com/27387830/169782337-678f2df0-080a-495a-b59f-a98c5a427cf8.png)

更多使用操作可参照[Exchangis1.0用户手册](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/zh_CN/ch1/exchangis_user_manual_cn.md)

### 5.Exchangis AppConn安装原理

Exchangis 的相关配置信息会插入到以下表中，通过配置下表，可以完成 Exchangis 的使用配置，安装 Exchangis AppConn 时，脚本会替换每个 AppConn 下的 init.sql，并插入到表中。(注：如果仅仅需要快速安装APPCONN，无需过分关注以下字段，提供的init.sql中大多以进行默认配置。重点关注以上操作即可)

| 表名 | 表作用 | 备注 |
| :----: | :----: |-------|
| dss_application	 | 应用表,主要是插入 exchangis 应用的基本信息	| 必须 |
| dss_menu | 菜单表，存储对外展示的内容，如图标、名称等 | 必须 |
| dss_onestop_menu_application| menu 和 application 的关联表，用于联合查找 | 必须 |
| dss_appconn |appconn 的基本信息，用于加载 appconn | 必须 |
| dss_appconn_instance| AppConn 的实例的信息，包括自身的url信息 | 必须 |
| dss_workflow_node | schedulis 作为工作流节点需要插入的信息	 | 必须 |

Exchangis 作为调度框架，实现了一级规范和二级规范，需要使用 exchangis AppConn 的微服务如下表。

| 表名 | 表作用 | 备注 |
| :----: | :----: |-------|
| dss-framework-project-server | 使用 exchangis-appconn 完成工程以及组织的统一| 必须 |
| dss-workflow-server | 借用调度 AppConn 实现工作流发布，状态等获取 | 必须 |
