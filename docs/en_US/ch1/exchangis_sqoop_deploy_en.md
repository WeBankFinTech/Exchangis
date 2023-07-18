# Sqoop engine uses documentation 
### Prepare the environment 
Sqoop engine is an indispensable component to perform Exchange IS data synchronization task, and only after the installation and deployment of Sqoop engine can it successfully perform data synchronization task. At the same time, make sure sqoop is installed on the deployed machine. 

Before you install and deploy Sqoop engine,  Please complete the installation of Exchangis 1.0.0 and related components according to the [Exchangis 1.0.0 installation and deployment document](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/en_US/ch1/exchangis_deploy_en.md), and ensure that the basic functions of the project are available. 

Sqoop engine mainly depends on Hadoop basic environment. If this node needs to deploy Sqoop engine, it needs to deploy Hadoop client environment. 

It is strongly recommended that you use the native Sqoop to perform the test task on this node before performing the Sqoop task, so as to check whether the environment of this node is normal. 

| Environment variable name | Environment variable content | Remark |
| :----: | :----: |-------|
| JAVA_HOME   | JDK installation path | Required |
| HADOOP_HOME     | Hadoop installation path | Required |
| HADOOP_CONF_DIR | Hadoop config path | Required |
| SQOOP_HOME | Sqoop installation path | Not Required |
| SQOOP_CONF_DIR | Sqoop config path | Not Required |
| HCAT_HOME | HCAT config path | Not Required |
| HBASE_HOME | HBASE config path | Not Required |


| Linkis system params        | Params                                                   | Remark                                                       |
| --------------------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| wds.linkis.hadoop.site.xml  | Set the location where sqoop loads hadoop parameter file | Required, please refer to the example："/etc/hadoop/conf/core-site.xml;/etc/hadoop/conf/hdfs-site.xml;/etc/hadoop/conf/yarn-site.xml;/etc/hadoop/conf/mapred-site.xml" |
| sqoop.fetch.status.interval | Set the interval for getting sqoop execution status      | Not Required, the default value is 5s.                       |
### Prepare installation package 
#### 1）Download binary package 

Exchangis1.1.2 and Linkis 1.4.0 support the mainstream Sqoop versions 1.4.6 and 1.4.7, and later versions may need to modify some codes for recompilation. 

[Click to jump to Release interface](https://github.com/WeBankFinTech/Exchangis/releases/tag/release-1.0.0)

#### 2）Compile and package 
If you want to develop and compile sqoop engine yourself, the specific compilation steps are as follows: 

1.clone Exchangis's source code

2.Under exchangis-plugins module, find sqoop engine and compile sqoop separately, as follows :

```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/engine/sqoop
mvn clean install
```
Then the sqoop engine installation package will be found in this path. 
```
{EXCHANGIS_CODE_HOME}\exchangis-plugins\sqoop\target\out\sqoop
```


### Start deployment
#### 1）Sqoop engine installation 
1、Get the packed sqoop.zip material package

2、Place it in the following directory and unzip it 

```
cd {LINKIS_HOME}/linkis/lib/linkis-engineconn-plugins
unzip.zip
```
The extracted directory structure is: 
```
dist
plugin
```
(Note, see which users the current sqoop engine has permissions on, not necessarily root) 


#### 2）Restart linkis-engineplugin service to make sqoop engine take effect
New engines joining linkis will not take effect until the engineplugin service of linkis is restarted, and the restart script is. /linkis-daemon.sh in the Linkis installation directory. The specific steps are as follows :
```
cd {LINKIS_INSTALL_HOME}/links/sbin/
./linkis-daemon.sh restart cg-engineplugin
```
After the service is successfully started, the installation and deployment of sqoop will be completed. 

For a more detailed introduction of engineplugin, please refer to the following article.   
https://linkis.apache.org/zh-CN/docs/latest/deployment/engine-conn-plugin-installation