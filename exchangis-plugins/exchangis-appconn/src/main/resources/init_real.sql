
select @dss_appconn_mlssId:=id from `dss_appconn` where `appconn_name` = 'mlss';
delete from `dss_appconn_instance` where  `appconn_id`=@dss_appconn_mlssId;

delete from `dss_appconn`  where `appconn_name`='mlss';
INSERT INTO `dss_appconn` (
    `appconn_name`,
    `is_user_need_init`,
    `level`,
    `if_iframe`,
    `is_external`,
    `reference`,
    `class_name`,
    `appconn_class_path`,
    `resource`)
VALUES (
           'mlss',
           0,
           1,
           NULL,
           0,
           NULL,
           'com.webank.wedatasphere.dss.appconn.mlss.MLSSAppConn',
           '/appcom/Install/DSSInstall/dss-1.1.3/dss-appconns/mlss',
           '');


select @dss_appconn_mlssId:=id from `dss_appconn` where `appconn_name` = 'mlss';




INSERT INTO `dss_appconn_instance`(
    `appconn_id`,
    `label`,
    `url`,
    `enhance_json`,
    `homepage_uri`)
VALUES (
           @dss_appconn_mlssId,
           'DEV',
           'http://10.107.127.19:30793/',
           '{"MLSS-SecretKey":"MLFLOW","MLSS-Auth-Type":"SYSTEM","MLSS-APPSignature":"MLFLOW","MLSS-BaseUrl":"http://10.107.127.19:30793","baseUrl":"http://10.107.127.19:30793","MLSS-ModelMonitoring-JAR":"/appcom/Install/quickml/qml_algo/hwenzan/qml_algo.jar"}',
           'http://10.107.127.19:30793/#/mlFlow');


INSERT INTO `dss_appconn_instance`(
    `appconn_id`,
    `label`,
    `url`,
    `enhance_json`,
    `homepage_uri`)
VALUES (
           @dss_appconn_mlssId,
           'PROD',
           'http://10.107.127.19:30793/',
           '{"MLSS-SecretKey":"MLFLOW","MLSS-Auth-Type":"SYSTEM","MLSS-APPSignature":"MLFLOW","MLSS-BaseUrl":"http://10.107.127.19:30793","baseUrl":"http://10.107.127.19:30793","MLSS-ModelMonitoring-JAR":"/appcom/Install/quickml/qml_algo/hwenzan/qml_algo.jar"}',
           'http://10.107.127.19:30793/#/mlFlow');

select @dss_mlssId:=name from `dss_workflow_node` where `node_type` = 'linkis.appconn.mlss';
delete from `dss_workflow_node_to_group` where `node_id`=@dss_mlssId;
--
delete from `dss_workflow_node` where `node_type`='linkis.appconn.mlss';
INSERT INTO `dss_workflow_node` (
    `icon_path`,
    `node_type`,
    `appconn_name`,
    `submit_to_scheduler`,
    `enable_copy`,
    `should_creation_before_node`,
    `support_jump`,
    `jump_type`,
    `name`)
VALUES (
           'icons/mlss.icon',
           'linkis.appconn.mlss',
           'mlss',
           1,
           0,
           0,
           1,
           1,
           'mlss');

--
select @dss_mlssId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.mlss';
insert  into `dss_workflow_node_to_group` (`node_id`, `group_id`) values (@dss_mlssId, 8);


select @dss_workflow_node_id:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.mlss';
INSERT INTO `dss_workflow_node_to_ui` (`workflow_node_id`, `ui_id`) VALUES
                                                                        (@dss_mlssId, 1),
                                                                        (@dss_mlssId, 4),
                                                                        (@dss_mlssId, 5),
                                                                        (@dss_mlssId, 6),
                                                                        (@dss_mlssId, 35),
                                                                        (@dss_mlssId, 36),
                                                                        (@dss_mlssId, 3);

