# Sqoop 引擎使用文档
### 环境准备
Sqoop引擎是执行Exchangis数据同步任务不可或缺的组件，只有安装部署完成Sqoop引擎才能够成功执行数据同步任务。同时，确保所部署的机器上有安装sqoop。

您在安装部署Sqoop引擎之前，请按照[Exchangis1.0.0安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/zh_CN/ch1/exchangis_deploy_cn.md)安装完成Exchangis1.0.0及相关组件的安装，并确保工程基本功能可用。

Sqoop引擎主要依赖Hadoop基础环境，如果该节点需要部署Sqoop引擎，需要部署Hadoop客户端环境。

强烈建议您在执行Sqoop任务之前，先在该节点使用原生的Sqoop执行测试任务，以检测该节点环境是否正常。

| 环境变量名  | 环境变量内容   | 备注      |
| :----: | :----: |-------|
| JAVA_HOME   | JDK安装路径  | 必须        |
| HADOOP_HOME     | Hadoop安装路径 | 必须       |
| HADOOP_CONF_DIR | Hadoop配置路径 | 必须        |
| SQOOP_HOME | Sqoop安装路径 | 非必须      |
| SQOOP_CONF_DIR | Sqoop配置路径 | 非必须   |
| HCAT_HOME | HCAT配置路径 | 非必须         |
| HBASE_HOME | HBASE配置路径 | 非必须 |


| Linkis系统参数              | 参数                            | 备注                                                         |
| --------------------------- | ------------------------------- | ------------------------------------------------------------ |
| wds.linkis.hadoop.site.xml  | 设置sqoop加载hadoop参数文件位置 | 必须，参考示例："/etc/hadoop/conf/core-site.xml;/etc/hadoop/conf/hdfs-site.xml;/etc/hadoop/conf/yarn-site.xml;/etc/hadoop/conf/mapred-site.xml" |
| sqoop.fetch.status.interval | 设置获取sqoop执行状态的间隔时间 | 非必须，默认值为5s                                           |
### 安装包准备
#### 1）下载二进制包

Exchangis1.0.0和Linkis 1.1.1支持的主流Sqoop版本1.4.6与1.4.7，更高版本可能需要修改部分代码重新编译。

[点击跳转 Release 界面](https://github.com/WeBankFinTech/Exchangis/releases/tag/release-1.0.0)

#### 2） 编译打包
如果您想自己开发和编译sqoop引擎，具体编译步骤如下：

1.clone Exchangis的代码

2.在exchangis-plugins模块下，找到sqoop引擎，单独编译sqoop，操作如下
```
cd {EXCHANGIS_CODE_HOME}/exchangis-engines/engine-plugins/sqoop
mvn clean install
```
然后会在该路径下找到sqoop引擎安装包
```
{EXCHANGIS_CODE_HOME}/exchangis-engines/engine-plugins/sqoop/target/out
```


### 开始部署
#### 1）sqoop引擎安装
1.拿到打包出来的sqoop物料包，目录结构为：

```shell
sqoop
-- dist
-- plugin
```

2.放置到linkis安装路径的如下目录

```shell
cd {LINKIS_HOME}/linkis-engineconn-plugins
```
（注意，看当前sqoop引擎对哪些用户有权限，不一定是root）


#### 2）重启linkis-engineplugin服务使sqoop引擎生效
新加入linkis的引擎都要重启linkis的engineplugin服务才会生效，重启脚本为linkis安装目录下的./sbin/linkis-daemon.sh，具体步骤如下
```shell
cd {LINKIS_INSTALL_HOME}/links/sbin/
./linkis-daemon.sh restart cg-engineplugin
```
待服务启动成功，在linkis数据库中校验sqoop引擎是否安装完毕

```yaml
select * from linkis_cg_engine_conn_plugin_bml_resources where engine_conn_type='sqoop';
```

至此，sqoop安装部署就完成了。

engineplugin更详细的介绍可以参看下面的文章。  
https://linkis.apache.org/zh-CN/docs/latest/deployment/engine-conn-plugin-installation/