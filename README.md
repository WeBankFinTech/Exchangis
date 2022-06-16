[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README-ZH.md)  

## Introduction

Exchangis 1.0.0 is a new version of data exchange tool jointly developed by WeDataSphere, a big data platform of WeBank, and community users, which supports the synchronization of structured and unstructured data transmission between heterogeneous data sources. 

Exchangis is abstracted a set of unified data source and synchronous job definition plugins, which allows users to quickly access new data sources, and can be used in the page only by simple configuration in the database. 

Based on the plug-in framework design and the computing middleware [Linkis](https://github.com/apache/incubator-Linkis), Exchangis can quickly integrate and dock Linkis's integrated data synchronization engine, and transform Exchangis's synchronization job into Linkis's data synchronization job.

With the help of [Linkis](https://github.com/apache/incubator-linkis) computing middleware's connection, reuse and simplification capabilities, Exchangia is inherently equipped with financial-grade data synchronization capabilities of high concurrency, high availability, multi-tenant isolation and resource control. 

###  Interface preview 

![image](https://user-images.githubusercontent.com/27387830/171488936-2cea3ee9-4ef7-4309-93e1-e3b697bd3be1.png)

## Core characteristics

### 1. Lightweight datasource management  

- Based on Linkis DataSource, all the necessary capabilities of the underlying datasource in Exchangis abstracted as the source and sink of a synchronous job. A datasource can be created by simple configuration.

- Special datasource version publishing management function supports version history datasource rollback, and one-click publishing does not need to configure historical datasources again. 


### 2. High-stability and fast-response data synchronization task execution 

- **Near-real-time task management**  
  Quickly capture information such as transmission task log and transmission rate, monitor and display various indicators of multi-task including CPU usage, memory usage, data synchronization record, etc., and support closing tasks in real time.

- **Task high concurrent transmission**  
  Multi-tasks are executed concurrently, and sub-tasks can be copied to show the status of each task in real time. Multi-tenant execution function can effectively prevent tasks from affecting each other during execution. 

- **Self-check of task status**  
  Monitor long-running tasks and abnormal tasks, stop tasks and release occupied resources in time.   


### 3. The first one-stop portal for big data development through DSS workflow. 

- Realize DSS AppConn's three-level specification, including the first-level SSO specification, the second-level organizational structure specification and the third-level development process specification;

- As the data exchange node of DSS workflow, it is the portal process in the whole workflow link, which provides a solid data foundation for the subsequent operation of workflow nodes；

## Overall Design 

### Architecture Design

![架构设计](https://user-images.githubusercontent.com/27387830/173026793-f1475803-9f85-4478-b566-1ad1d002cd8a.png)


## Documents

[Quick Deploy](https://github.com/WeDataSphere/Exchangis/blob/dev-1.0.0-rc/docs/zh_CN/ch1/exchangis_deploy_cn.md)  
[User Manual](https://github.com/WeDataSphere/Exchangis/blob/dev-1.0.0-rc/docs/zh_CN/ch1/exchangis_user_manual_cn.md)

## Communication and contribution 

If you want to get the fastest response, please mention issue to us, or scan the code into the group ：

![communication](images/en_US/ch1/communication.png)

## License

Exchangis is under the Apache 2.0 License. See the [License](../../../LICENSE) file for details.

