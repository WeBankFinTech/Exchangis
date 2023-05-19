# ExchangisAppConn  installation documentation 

This paper mainly introduces the deployment, configuration and installation of ExchangisAppConn in DSS(DataSphere Studio)1.0.1.

### 1. Preparations for the deployment of ExchangisAppConn 
Before you deploy ExchangisAppConn, please follow the [Exchangis1.0.0 to install the deployment document](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/en_US/ch1/exchangis_deploy_en.md) to complete the installation of Exchangis1.0.0 and other related components, and ensure that the basic functions of the project are available. 

### 2. Download and compilation of the ExchangisAppConn plugin 
#### 1） Download binary package 
We provide ExchangisAppconn's material package, which you can download and use directly. [Click to jump to Release interface](https://github.com/WeBankFinTech/Exchangis/releases)
#### 2）  Compile and package 

If you want to develop and compile ExchangisAppConn yourself, the specific compilation steps are as follows: 
1.clone Exchangis's source code
2.In exchangis-plugins module, find exchangis-appconn, separate compilation exchangis-appconn

```
cd {EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn
mvn clean install
```
The exchangis-appconn.zip installation package will be found in this path. 
```
{EXCHANGIS_CODE_HOME}/exchangis-plugins/exchangis-appconn/target/exchangis-appconn.zip
```

### 3. Overall steps for deployment and configuration of ExchangisAppConn
1、Get the packed exchangis-appconn.zip material package. 

2、Place it in the following directory and unzip it

```
cd {DSS_Install_HOME}/dss/dss-appconns
unzip exchangis-appconn.zip
```
 The extracted directory structure is: 
```
conf
db
lib
appconn.properties
```

3、 Execute scripts for automated installation

```shell
cd {DSS_INSTALL_HOME}/dss/bin
./install-appconn.sh
# Script is an interactive installation scheme. You need to enter the string exchangis and the ip and port of exchangis service to complete the installation.
# Exchangis port here refers to the front-end port, which is configured in nginx. Rather than the back-end service port.
```

### 4. After the installation of Exchangis-AppConn is completed, the dss service needs to be re-installed to finally complete the plug-in update. 

#### 4.1） Make the deployed APPCONN effective 
Make APPCONN effective by using DSS start-stop script, which is located in {DSS_INSTALL_HOME}/sbin, and execute the script by using the following commands in turn 
```
sh /sbin/dss-stop-all.sh
sh /sbin/dss-start-all.sh
```
There may be startup failure or jam in the middle, so you can quit repeated execution. 

#### 4.2） Verify that exchangis-appconn is effective. 
After the exchangis-appconn is installed and deployed, the following steps can be taken to preliminarily verify whether exchangis-appconn is successfully installed. 
1.	 Create a new project in DSS workspace 
![image](https://user-images.githubusercontent.com/27387830/169782142-b2fc2633-e605-4553-9433-67756135a6f1.png)

2.	 Check whether the project is created synchronously on Exchangis. Successful creation means successful installation of appconn 
![image](https://user-images.githubusercontent.com/27387830/169782337-678f2df0-080a-495a-b59f-a98c5a427cf8.png)

For more operation, please refer to [Exchangis 1.0 User Manual](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0-rc/docs/zh_CN/ch1/exchangis_user_manual_cn.md)

### 5.Exchangis AppConn installation principle 

The related configuration information of Exchangis inserted into the following table. By configuring the following table, you can complete the use configuration of Exchangis. When installing Exchangis AppConn, the script will replace init.sql under each AppConn and insert it into the table. (Note: If you only need to install APPCONN quickly, you don't need to pay too much attention to the following fields. Most of the provided init.sql are configured by default. Focus on the above operations) 

|          Table name          |                        Table function                        | Remark   |
| :----: | :----: |-------|
| dss_application	 | The application table is mainly used to insert the basic information of Exchangis application	| Required |
| dss_menu | Menu, which stores the displayed contents, such as icons, names, etc | Required |
| dss_onestop_menu_application| And menu and application, which are used for joint search | Required |
| dss_appconn |Basic information of appconn, used to load appconn | Required |
| dss_appconn_instance| Information of an instance of AppConn, including its own url information | Required |
| dss_workflow_node | Schedulis is the information that needs to be inserted as a workflow node	| Required |

Exchangis as a scheduling framework, which implements the first-level specification and the second-level specification. The micro-services of exchangis AppConn need to be used in the following table. 

| Table name | Table function | Remark |
| :----: | :----: |-------|
| dss-framework-project-server | Use exchangis-appconn to complete the project and unify the organization. | Required |
| dss-workflow-server | Scheduling AppConn is used to achieve workflow publishing and status acquisition. | Required |
