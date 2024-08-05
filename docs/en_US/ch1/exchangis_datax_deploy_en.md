# DataX engine uses documentation 

### Prepare the environment 

The DataX engine is an indispensable component for executing Exchangis data synchronization tasks. Data synchronization tasks can be performed only after the DataX engine is installed and deployed. Also, ensure that DataX is installed on the deployed machine.

Before you install and deploy DataX engine,  Please complete the installation of Exchangis and related components according to the [Exchangis installation and deployment document](docs/en_US/ch1/exchangis_deploy_en.md), and ensure that the basic functions of the project are available. 

It is strongly recommended that you use the native DataX to perform the test task on this node before performing the DataX task, so as to check whether the environment of this node is normal. 

| Environment variable name | Environment variable content | Remark       |
| :-----------------------: | :--------------------------: | ------------ |
|         JAVA_HOME         |    JDK installation path     | Required     |
|        DATAX_HOME         |   DataX installation path    | Not Required |
|      DATAX_CONF_DIR       |      DataX config path       | Not Required |

### Prepare installation package 

#### 1）Download binary package 

Exchangis1.1.2 and Linkis 1.4.0 support the mainstream DataX versions 1.4.6 and 1.4.7, and later versions may need to modify some codes for recompilation. 

[Click to jump to Release interface](https://github.com/WeBankFinTech/Exchangis/releases/tag/release-1.1.2)

#### 2）Compile and package 

If you want to develop and compile datax engine yourself, the specific compilation steps are as follows: 

1.clone Exchangis's source code

2.Under exchangis-plugins module, find sqoop engine and compile sqoop separately, as follows :

```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/engine/datax
mvn clean install
```

Then the datax engine installation package will be found in this path. 

```
{EXCHANGIS_CODE_HOME}\exchangis-plugins\datax\target\out\datax
```


### Start deployment

#### 1）DataX engine installation 

1、Get the packed datax.zip material package, the directory structure is

```shell
datax
-- dist
-- plugin
```

2、Place in the following directory in the linkis installation path

```shell
cd {LINKIS_HOME}/linkis/lib/linkis-engineconn-plugins
```

(Note that depending on which users the current datax engine has permissions for, they are generally hadoop user groups and hadoop users)


#### 2）Restart linkis-engineplugin service to make datax engine take effect

New engines joining linkis will not take effect until the engineplugin service of linkis is restarted, and the restart script is. /linkis-daemon.sh in the Linkis installation directory. The specific steps are as follows :

```
cd {LINKIS_INSTALL_HOME}/links/sbin/
./linkis-daemon.sh restart cg-engineplugin
```

After the service is successfully started, check whether the datax engine is installed in the linkis database

```shell
select * from linkis_cg_engine_conn_plugin_bml_resources where engine_conn_type='datax';
```

At this point, the datax installation and deployment is complete.

For a more detailed introduction of engineplugin, please refer to the following article.   
https://linkis.apache.org/zh-CN/docs/latest/deployment/install-engineconn