# Exchangis Component Upgrade Documentation
This article mainly introduces the upgrade steps for adapting DSS1.1.2 and Linkis1.4.0 on the basis of the original installation of the Exchangis service. The biggest difference between the Exchangis1.0.0 and the Exchangis1.0.0-rc1 version is the installation of the ExchangisAppconn, which needs to be replaced by the entire Exchangisappconn. and load### 1.升级Exchangis前的工作
Before you upgrade Exchangis, please follow the[DSS1.1.2Install and deploy documentation](https://github.com/WeBankFinTech/DataSphereStudio-Doc/tree/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2)
and [Linkis1.4.0Install and deploy documentation](https://linkis.staged.apache.org/zh-CN/docs/1.4.0/deployment/deploy-quick)Complete the installation and upgrade of DSS and Linkis

### 2.Exchangis upgrade steps

#### 1）Delete old version ExchangisAppconn package

Go to the following directory and find exchangis appconn folder and delete:
```
{DSS_Install_HOME}/dss/dss-appconns
```

#### 2）Download binary package
We provide the upgrade material package of ExchangisAppconn, which you can download and use directly.[Click to jump Release interface](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/WeDatasphere/Exchangis/exchangis1.0.0-rc/exchangis-appconn.zip)

#### 3） Compile and package

If you want to compile ExchangisAppConn by yourself, the specific compilation steps are as follows:

1.clone Exchangis code
2.Under the exchangis-plugins module, find exchangis-appconn and compile exchangis-appconn separately
```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn
mvn clean install
```
The exchangis-appconn.zip installation package will be found in this path
```
{EXCHANGIS_CODE_HOME}\exchangis-plugins\exchangis-appconn\target\exchangis-appconn.zip
```

### 3.ExchangisAppConn general steps to deploy and configure plugins
1.Get the packaged exchangis-appconn.zip material package

2.Put it in the following directory and unzip it

```
cd {DSS_Install_HOME}/dss/dss-appconns
unzip exchangis-appconn.zip
```
The decompressed directory structure is:
```
conf
db
lib
appconn.properties
```

3.Execute a script to automate the installation

```shell
cd {DSS_INSTALL_HOME}/dss/bin
./install-appconn.sh
# The script is an interactive installation scheme, you need to enter the string exchangis and the ip and port of the exchangis service to complete the installation
# The exchangis port here refers to the front-end port, which is configured in nginx. instead of the backend service port
```

### 4.After completing the installation of exchangis-appconn, call the script to refresh the appconn service

#### 4.1）Make the deployed APPCONN take effect
Use DSS refresh to make APPCONN take effect, enter the directory where the script is located {DSS_INSTALL_HOME}/bin, and execute the script with the following command. Note that there is no need to restart the dss service:
```
sh ./appconn-refresh.sh
```

#### 4.2）Verify that exchangis-appconn is in effect
After the installation and deployment of exchangis-appconn is completed, you can preliminarily verify whether the exchangis-appconn is successfully installed by performing the following steps.
1.	Create a new project in the DSS workspace
![image](https://user-images.githubusercontent.com/27387830/169782142-b2fc2633-e605-4553-9433-67756135a6f1.png)

2.	Check whether the project is created synchronously on the exchange side. If the creation is successful, the appconn installation is successful.
![image](https://user-images.githubusercontent.com/27387830/169782337-678f2df0-080a-495a-b59f-a98c5a427cf8.png)

For more usage, please refer to[Exchangis1.0 User Manual](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.1-rc/docs/zh_CN/ch1/exchangis_user_manual_cn.md)
