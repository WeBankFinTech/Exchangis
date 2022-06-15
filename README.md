[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README-ZH.md)  

## Introduction
Exchangis is a lightweight,highly extensible data exchange platform that supports data transmission between structured and unstructured heterogeneous data sources. On the application layer, it has business features such as data permission management and control, high availability of node services and multi-tenant resource isolation. On the data layer, it also has architectural characteristics such as diversified transmission architecture, module plug-in and low coupling of components.

Exchnagis's transmission and exchange capabilities depend on its underlying aggregated transmission engines. It defines a unified parameter model for various data sources on the top layer. It maps and configures the parameter model for each transmission engine, and then converts it into the engine's input model. Each type of engine will add Exchangis features, and  the enhancement of certain engine features will improve the Exchangis features. Exchangis's default engine aggregated and enhanced is Alibaba's DataX transmission engine.

## Features
- **Data Source Management**  
Share your own data source in a bound project；  
Set the external authority of the data source to control the inflow and outflow of data。

- **Muti-transport Engine Support**  
Transmission engine scales horizontally；  
The current version fully aggregates the offline batch engine DataX and partially aggregates the big data batch derivative engine SQOOP

- **Near Real-time Task Control**  
Quickly capture the transmission task log, transmission rate and other information, close the task in real time；  
Dynamically limit transmission rate based on bandwidth  

- **Support Unstructured Transmission**  
Transform the DataX framework and build a binary stream fast channel separately, suitable for pure data synchronization scenarios without data conversion。

- **Task Status Self-check**  
Monitor long-running tasks and tasks with abnormal status, release occupied resources in time and issue alarms。  

## Comparison With Existing Systems
Comparison of some existing data exchange tools and platforms：  

| Function module | Description | Exchangis | DataX | Sqoop | DataLink | DBus |
| :----: | :----: |-------|-------|-------|-------|-------|
| UI | Integrated the convenient management interface and monitoring window | Integrated | None | None | Integrated |Integrated |
| Installation and deployment | Ease of deployment and third-party dependencies | One-click deployment, no dependencies | No dependencies | Rely on Hadoop environment | Rely on Zookeeper | Rely on a large number of third-party components |
| Data authority management |  Multi-tenant permission configuration and data source permission control | Support | Not support | Not support | Not support | Support |
|        |Dynamic limit transmission | Support | Partially supported, unable to adjust dynamically | Partially supported, unable to adjust dynamically | Support | Support，with Kafka |
| Data transmission| Unstructured data binary transmission | Support, fast channel | Not support | Not support | Not support，only transport record | Not support，need to be converted to a unified message format|
|        | Embed processing code | Support，dynamic compilation | Not support | Not support | Not support | Partial support |
|        | Transmission breakpoint recovery | Support（Not open source） | Not support | Not support | Support | Support |
| High availability | Mutiple services, failure does not affect the use | Application high availability, transmission single point（Distributed architecture planning） | Single point service（Open source version） | Multipoint transmission | Application、transmission high availability | Application、transmission high availability |
| System Management | Nodes、resources management | Support | Not support | Not support | Support | Support |

## Overall Design

### Architecture

![Architecture](images/en_US/ch1/architecture.png)

## Documents
[Quick Deploy](docs/zh_CN/ch1/exchangis_deploy_cn.md)  
[User Manual](docs/zh_CN/ch1/exchangis_user_manual_cn.md)

## Communication

If you desire immediate response, please kindly raise issues to us or scan the below QR code by WeChat and QQ to join our group:

![Communication](./images/zh_CN/ch1/communication.JPG)

## License

Exchangis is under the Apache 2.0 License. See the [License](LICENSE) file for details.