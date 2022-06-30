# Exchangis

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

[English](README.md) | 中文  

## 介绍

Exchangis 1.0.0 是微众银行大数据平台 WeDataSphere 与社区用户共同研发的的新版数据交换工具，支持异构数据源之间的结构化和非结构化数据传输同步。

Exchangis 抽象了一套统一的数据源和同步作业定义插件，允许用户快速接入新的数据源，并只需在数据库中简单配置即可在页面中使用。

基于插件化的框架设计，及计算中间件 [Linkis](https://github.com/apache/incubator-linkis)，Exchangis 可快速集成对接 Linkis 已集成的数据同步引擎，将 Exchangis 的同步作业转换成 Linkis 数据同步引擎的数据同步作业。

借助于 [Linkis](https://github.com/apache/incubator-linkis) 计算中间件的连接、复用与简化能力，Exchangis 天生便具备了高并发、高可用、多租户隔离和资源管控的金融级数据同步能力。

### 界面预览

![image](https://user-images.githubusercontent.com/27387830/171488936-2cea3ee9-4ef7-4309-93e1-e3b697bd3be1.png)

## 核心特点

### 1. 轻量化的数据源管理  

- 基于 Linkis DataSource，抽象了底层数据源在 Exchangis 作为一个同步作业的 Source 和 Sink 所必须的所有能力。只需简单配置即可完成一个数据源的创建。

- 特别数据源版本发布管理功能，支持历史版本数据源回滚，一键发布无需再次配置历史数据源。


### 2.  高稳定，快响应的数据同步任务执行

- **近实时任务管控**  
快速抓取传输任务日志以及传输速率等信息，对多任务包括CPU使用、内存使用、数据同步记录等各项指标进行监控展示，支持实时关闭任务；  

- **任务高并发传输**  
多任务并发执行，并且支持复制子任务，实时展示每个任务的状态，多租户执行功能有效避免执行过程中任务彼此影响进行；

- **任务状态自检**  
监控长时间运行的任务和状态异常任务，中止任务并及时释放占用的资源。  


### 3.  与DSS工作流打通，一站式大数据开发的门户

- 实现DSS AppConn包括一级 SSO 规范，二级组织结构规范，三级开发流程规范在内的三级规范；

- 作为DSS工作流的数据交换节点，是整个工作流链路中的门户流程，为后续的工作流节点运行提供稳固的数据基础；

## 整体设计

### 架构设计

![架构设计](https://user-images.githubusercontent.com/27387830/173026793-f1475803-9f85-4478-b566-1ad1d002cd8a.png)


## 相关文档
[安装部署文档](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/zh_CN/ch1/exchangis_deploy_cn.md)  
[用户手册](https://github.com/WeBankFinTech/Exchangis/blob/dev-1.0.0/docs/zh_CN/ch1/exchangis_user_manual_cn.md)

## 交流贡献

如果您想得到最快的响应，请给我们提 issue，或者扫码进群：

![communication](images/zh_CN/ch1/communication.png)

## License

Exchangis is under the Apache 2.0 License. See the [License](../../../LICENSE) file for details.
