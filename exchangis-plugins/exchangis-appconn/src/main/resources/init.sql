-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新

-- 删除exchangis关联的数据 --
delete from `dss_appconn_instance` where `appconn_id` in (select `id` from `dss_appconn` where `appconn_name` = 'exchangis');
delete from  `dss_workspace_menu_appconn` where `appconn_id` in (select `id` from `dss_appconn` where `appconn_name` = 'exchangis');

delete from `dss_appconn`  where `appconn_name`='exchangis';
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('exchangis', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.exchangis.dss.appconn.ExchangisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/exchangis', '');
select @dss_appconn_exchangis_id:=id from `dss_appconn` where `appconn_name` = "exchangis";

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@dss_appconn_exchangis_id, 'DEV', 'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/', '', '#/projectManage');

-- 看appconn组件是要归属于哪个菜单
select @exchangis_menuId:=id from `dss_workspace_menu` where `name` = "数据交换";
INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
 VALUES(@dss_appconn_exchangis_id,@exchangis_menuId,'Exchangis','Exchangis','Exchangis',''
 ,'exchangis, statement','数据交换,数据源','1','enter Exchangis','进入Exchangis','user manual','用户手册','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/#/projectManage','shujukeshihua-logo',NULL,NULL,NULL,NULL,NULL,'shujukeshihua-icon');

-- Sqoop节点安装
select @old_dss_exchangis_sqoopId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop';
select @old_dss_exchangis_dataxId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax';
delete from `dss_workflow_node`  where `node_type` like '%exchangis%';
delete from `dss_workflow_node_to_group` where `node_id`=@old_dss_exchangis_sqoopId or `node_id`=@old_dss_exchangis_dataxId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@old_dss_exchangis_sqoopId or `workflow_node_id`=@old_dss_exchangis_dataxId;

-- 节点表dss_workflow_node
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('sqoop','exchangis','linkis.appconn.exchangis.sqoop',1,'1','1','0','1','icons/sqoop.icon');
select @dss_exchangis_sqoopId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.sqoop';
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('datax','exchangis','linkis.appconn.exchangis.datax',1,'1','1','0','1','icons/datax.icon');
select @dss_exchangis_dataxId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.exchangis.datax';

-- 节点组表dss_workflow_node_to_group
select @dss_workflow_node_group_id:=id from `dss_workflow_node_group` where `name` = '数据交换';
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_exchangis_sqoopId, @dss_workflow_node_group_id);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_exchangis_dataxId, @dss_workflow_node_group_id);

-- 节点UI表dss_workflow_node_to_ui
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_sqoopId, 1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_sqoopId, 2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_sqoopId, 3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_sqoopId, 4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_sqoopId, 5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_dataxId, 1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_dataxId, 2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_dataxId, 3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_dataxId, 4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_exchangis_dataxId, 5);
