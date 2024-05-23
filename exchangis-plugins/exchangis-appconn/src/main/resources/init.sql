-- 删除exchangis关联的数据 --
delete from `dss_appconn_instance` where `appconn_id` in (select `id` from `dss_appconn` where `appconn_name` = 'exchangis');
delete from  `dss_workspace_menu_appconn` where `appconn_id` in (select `id` from `dss_appconn` where `appconn_name` = 'exchangis');
delete from `dss_appconn`  where `appconn_name`='exchangis';

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('exchangis', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.exchangis.dss.appconn.ExchangisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/exchangis', '');

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES ((select id from `dss_appconn` where `appconn_name` = "exchangis" limit 1), 'SIT', 'http://10.107.116.246:9090/', '', '#/projectManage');

-- 看appconn组件是要归属于哪个菜单
INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES((select id from `dss_appconn` where `appconn_name` = "exchangis" limit 1), (select id from `dss_workspace_menu` where `name` = "数据交换")
,'Exchangis','Exchangis','Exchangis','','exchangis, statement','数据交换,数据源','1','enter Exchangis','进入Exchangis','user manual','用户手册','http://10.107.116.246:9090/#/projectManage','shujujiaohuan-logo',NULL,NULL,NULL,NULL,NULL,'shujujiaohuan-icon');

-- 卸载节点
delete from `dss_workflow_node_to_group` where `node_id` in (select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop' or `node_type` = 'linkis.appconn.exchangis.datax');
delete from `dss_workflow_node_to_ui` where `workflow_node_id` in (select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop' or `node_type` = 'linkis.appconn.exchangis.datax');
delete from `dss_workflow_node`  where `node_type` like '%exchangis%';

-- 节点表dss_workflow_node
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('sqoop','exchangis','linkis.appconn.exchangis.sqoop',1,'1','1','0','1','icons/sqoop.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('datax','exchangis','linkis.appconn.exchangis.datax',1,'1','1','0','1','icons/datax.icon');

-- 节点组表dss_workflow_node_to_group
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), (select id from `dss_workflow_node_group` where `name` = '数据交换'));
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), (select id from `dss_workflow_node_group` where `name` = '数据交换'));

-- 节点UI表dss_workflow_node_to_ui
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), 1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), 2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), 3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), 4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop'), 5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), 1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), 2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), 3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), 4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax'), 5);
