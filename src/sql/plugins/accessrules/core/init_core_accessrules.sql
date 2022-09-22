
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'ACCESSRULES_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ACCESSRULES_MANAGEMENT','myluteceaccessrules.adminFeature.ManageRules.name',1,'jsp/admin/plugins/myluteceaccessrules/ManageRules.jsp','myluteceaccessrules.adminFeature.ManageRules.description',0,'accessrules',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ACCESSRULES_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ACCESSRULES_MANAGEMENT',1);

INSERT INTO core_datastore VALUES ('myluteceaccessrules.site_property.application_code', "");
