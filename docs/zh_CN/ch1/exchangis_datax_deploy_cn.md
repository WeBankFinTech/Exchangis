# DataX 引擎使用文档
### 环境准备
DataX引擎是执行Exchangis数据同步任务不可或缺的组件，只有安装部署完成DataX引擎才能够成功执行数据同步任务。同时，确保所部署的机器上有安装DataX。

您在安装部署DataX引擎之前，请按照[Exchangis1.1.2安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.1.2/docs/zh_CN/ch1/exchangis_deploy_cn.md)（TODO 待修改连接地址）安装完成Exchangis1.1.2及相关组件的安装，并确保工程基本功能可用。

强烈建议您在执行DataX任务之前，先在该节点使用原生的DataX执行测试任务，以检测该节点环境是否正常。

| 环境变量名  | 环境变量内容   | 备注      |
| :----: | :----: |-------|
| JAVA_HOME   | JDK安装路径  | 必须        |
| DataX_HOME | DataX安装路径 | 非必须      |
| DataX_CONF_DIR | DataX配置路径 | 非必须   |

### 安装包准备
#### 1）下载二进制包

Exchangis1.1.2和Linkis 1.4.0支持的主流DataX版本1.4.6与1.4.7，更高版本可能需要修改部分代码重新编译。

[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases/tag/release-1.1.2)（TODO 待修改连接地址）

#### 2） 编译打包
如果您想自己开发和编译datax引擎，具体编译步骤如下：

1、克隆Exchangis的代码

2、在exchangis-plugins模块下，找到datax引擎，单独编译datax，操作如下

```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/engine/datax
mvn clean install
```
然后会在该路径下找到datax引擎安装包
```
{EXCHANGIS_CODE_HOME}/exchangis-plugins/datax/target/out
```


### 开始部署
#### 1）DataX引擎安装
1、拿到打包出来的datax物料包，目录结构为：

```shell
datax
-- dist
-- plugin
```

2、放置到linkis安装路径的如下目录

```shell
cd {LINKIS_HOME}/linkis/lib/linkis-engineconn-plugins
```
（注意，看当前datax引擎对哪些用户有权限，一般都为hadoop用户组和hadoop用户）


#### 2）重启linkis-engineplugin服务使datax引擎生效
新加入linkis的引擎都要重启linkis的engineplugin服务才会生效，重启脚本为linkis安装目录下的./sbin/linkis-daemon.sh，具体步骤如下
```shell
cd {LINKIS_INSTALL_HOME}/links/sbin/
./linkis-daemon.sh restart cg-engineplugin
```
待服务启动成功，在linkis数据库中校验datax引擎是否安装完毕

```shell
select * from linkis_cg_engine_conn_plugin_bml_resources where engine_conn_type='datax';
```

至此，datax安装部署就完成了。

engineplugin更详细的介绍可以参看下面的文章。  
https://linkis.apache.org/zh-CN/docs/latest/deployment/engine-conn-plugin-installation/