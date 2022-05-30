Exchangis-1.0.0-RC版本包含所有 Project Exchangis-1.0.0-RC。

本次发布是Exchangis数据同步任务平台的全新RC版本，支持不同类型数据源之间的数据同步。平台将数据交换流程进行拆分，抽象出数据源，数据交换任务，任务调度等概念，加强了与linkis，DSS之间的联动，是0.X版本之后的重构版本。

该版本主要包含了以下内容：

1、【提交数据同步作业】数据同步作业执行重构

2、【子任务运行情况】对正在运行或者已完成的子作业查看他们的各项指标信息，以便对任务进行实时监控。

3、【获取实时日志】数据同步进行中/结束能展示job和task的实时日志，准确快速的排查问题

4、【数据源管理模块】对数据同步数据源进行一系列的管理操作

5、【同步历史模块】对已完成或正在执行的同步数据任务进行历史信息管理

6、【项目管理模块】对数据同步项目、任务进行配置管理

7、【数据同步作业构建模块】对数据同步作业配置的信息进行构建，形成能够提交给linkis的形式

8、 Exchangis1.0 Appconn对接DSS，支持工作流形式执行数据同步任务

## 缩写：

EJS: Exchangis Job Server
EJB:Exchangis Job Builder
EP:Exchangis Project
EDS:Exchangis Datasource Server
EXAPP:Exchangis Appconn

##  新特性：

- [EJS][[Exchangis-102]](https://github.com/WeBankFinTech/Exchangis/pull/102) 增加新的exchangis-job-server模块，新增对数据同步任务调度类、执行类、提交任务类，新政任务增删改查功能、日志监控功能、同步历史管理功能  
- [EJB][[Exchangis-110]](https://github.com/WeBankFinTech/Exchangis/pull/110) 增加新的exchangis-job-builder模块，新增对数据同步作业的构建管理类，将数据同步作业转换为能够提交给linkis的形式. 
- [EP][[Exchangis-86]](https://github.com/WeBankFinTech/Exchangis/issues/86) 增加新的exchangis-project模块，提供数据同步项目管理服务，通过restful接口提供了数据同步项目的增删查改等功能。
- [EDS][[Exchangis-95]](https://github.com/WeBankFinTech/Exchangis/pull/95) 增加新的exchangis-datasource-server模块，提供对linkis-datasouce的数据源管理服务，通过restful接口提供了对数据源的增删改查、连接测试、版本管理等功能。
- [EJS][[Exchangis-]](https://github.com/WeBankFinTech/Exchangis/pull/160) 新增加对已提交数据同步任务的的管理服务，通过restful接口提供了历史任务的日志监控，指标信息查看，中止已提交任务等功能。 
- [EJS][[Exchangis-131]](https://github.com/WeBankFinTech/Exchangis/pull/131) 对数据同步作业的执行过程进行了重构，提供异步执行、多租户功能和任务的高可用性
- [EXAPP][[Exchangis-179]](https://github.com/WeBankFinTech/Exchangis/pull/179) 增加新的exchangis-appconn模块，通过appconn以第三方组件的方式与DSS对接，实现工作流形式执行数据同步任务，同步增删改查任务等功能。

## 功能增强：

- [EJS][[Exchangis-140]](https://github.com/WeBankFinTech/Exchangis/pull/140) 对于正在运行/已完成的某个子作业，能够展示各项指标信息。
- [Exchangis][[Exchangis-284]](https://github.com/WeBankFinTech/Exchangis/pull/284) 安装部署脚本优化。
- [Exchangis][[Exchangis-284]](https://github.com/WeBankFinTech/Exchangis/pull/284) 完善exchangis ddl，dml，配置文件。
- [Exchangis][[Exchangis-281]](https://github.com/WeBankFinTech/Exchangis/pull/281) 添加对ISSUE模板、Pull request模板和github CI action的支持。
- [Exchangis][[Exchangis-224]](https://github.com/WeBankFinTech/Exchangis/pull/224) 升级exchangis对linkis的依赖版本由1.0.3到1.1.0。
- [EJS][[Exchangis-222]](https://github.com/WeBankFinTech/Exchangis/pull/222) 重构job-server模块以适配新的数据库表。
- [EP][[Exchangis-189]](https://github.com/WeBankFinTech/Exchangis/pull/189) 重构数据同步项目管理模块。
- [EJB][[Exchangis-188]](https://github.com/WeBankFinTech/Exchangis/pull/188) 增强sqoop引擎数据同步作业构建功能。
- [EXAPP][[Exchangis-179]](https://github.com/WeBankFinTech/Exchangis/pull/179) 重构exchangis appconn模块
- [EJS][[Exchangis-140]](https://github.com/WeBankFinTech/Exchangis/pull/140) 数据同步进行中/结束能展示job/和task的实时日志。
- [EJS,EP][[Exchangis-167]](https://github.com/WeBankFinTech/Exchangis/pull/167) 添加作业更新接口，添加作业及项目的分页功能，优化restful层 
- [Exchangis][[Exchangis-141]](https://github.com/WeBankFinTech/Exchangis/pull/141) 添加对数据同步任务日志的前端异常处理 
- [EJS][[Exchangis-143]](https://github.com/WeBankFinTech/Exchangis/pull/143) 添加获取任务日志、获取所有所有历史任务和杀死任务的接口 
- [EJS][[Exchangis-147]](https://github.com/WeBankFinTech/Exchangis/pull/147) 调整日志组件的获取逻辑 
- [EJS][[Exchangis-150]](https://github.com/WeBankFinTech/Exchangis/pull/150) 增加对hive分区信息获取的功能 
- [EDS][[Exchangis-165]](https://github.com/WeBankFinTech/Exchangis/pull/165) 客户端RPC的实现和restful类的命名空间的修改 

## Bug修复：

- [[Exchangis-227]](https://github.com/WeBankFinTech/Exchangis/pull/227) [EJS] 解决job info服务因库表变动而造成的不适配问题 

- [[Exchangis-185]](https://github.com/WeBankFinTech/Exchangis/pull/185) [EJS-Execution] 避免map计算方法中netscape调用过程中的死锁:调整调度任务选择规则 

- [[Exchangis-158]](https://github.com/WeBankFinTech/Exchangis/pull/158) [EJS-Execution] 修复更新进度、状态和指标的错误。

- [[Exchangis-165]](https://github.com/WeBankFinTech/Exchangis/pull/165) [EDS] 客户端RPC的实现和restful类的命名空间的修改。

- [[Exchangis-292]](https://github.com/WeBankFinTech/Exchangis/issues/292) [EJS] 修复hive的run_date时间分区系统变量获取失败的问题

- [[Exchangis-293]](https://github.com/WeBankFinTech/Exchangis/issues/293) [EJS-Execution] 修复sqoop工作流节点在取消任务后依旧在后台执行的问题 

- [[Exchangis-282]](https://github.com/WeBankFinTech/Exchangis/pull/282) [EJS-Execution] 修复复制子任务后更改数据源库表，导致原子任务的字段映射被覆盖的问题 

- [[Exchangis-140]](https://github.com/WeBankFinTech/Exchangis/issues/294) [EJS-Execution] 修复将任务提交到wtss进行调度代理用户失效的问题 

- [[Exchangis-241]](https://github.com/WeBankFinTech/Exchangis/issues/241) [EJS] 修复子任务日志缺失问题

- [[Exchangis-221]](https://github.com/WeBankFinTech/Exchangis/issues/221) [EJS] 修复查看子任务时候的重复请求问题 

- [[Exchangis-204]](https://github.com/WeBankFinTech/Exchangis/issues/204) [EJB] 在任务配置选择数据源库表时候，增加搜索框进行快速搜索 

- [[Exchangis-210]](https://github.com/WeBankFinTech/Exchangis/issues/210) [EJS] 修复点击执行ID查看任务进度后关闭，及时切换到别的页面，依旧在不停的拉取任务进度的问题 



  

## 贡献者：

EXCHANGIS 1.0.0-RC发布离不开EXCHANGIS社区的贡献者，感谢所有的社区贡献者！包括但不仅限于以下Contributors：wushengyeyouya、Dlimeng、Davidhua1996、mingfengwang、yuxin-No1、ryanqin01、
lucaszhu2zgf、FinalTarget、Liveipool 刚哥，杰哥，龙哥，嘉颖，坚哥chin