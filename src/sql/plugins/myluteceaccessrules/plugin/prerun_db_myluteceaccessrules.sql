-- liquibase formatted sql

--
-- Pre-execution script of the plugin : executed in the preliminary liquibase update, before the main
-- changelog is built and filtered, so that the version resolution of the main run sees its effects.
--
-- Until the SQL directory was named after the plugin (it was accessrules), plugin-liquibase could not
-- resolve the plugin and never recorded its version. On a database where liquibase never ran the plugin
-- scripts (installed with Ant), the plugin is then seen as a fresh install : create_db drops the rules
-- table, init_core stops on a duplicate key. The version is recorded from the schema instead, so that
-- the main run sees an installed plugin and runs the upgrade scripts it lacks.
--
-- Preconditions : the plugin is installed (its admin right exists) and has no recorded version ; the
-- first changeset needs the column added by 1.0.2, the second only the table. False on a fresh install
-- (the queries fail on an empty database : onError:MARK_RAN). The inserts carry the same condition : an
-- Ant build that does not exclude this file runs it as plain SQL.
--
-- changeset myluteceaccessrules:prerun-installed-version-1.0.2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(DISTINCT 1) FROM core_admin_right WHERE id_right = 'ACCESSRULES_MANAGEMENT' AND NOT EXISTS (SELECT 1 FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.myluteceaccessrules.version') AND (SELECT COUNT(encodebackurl) FROM mylutece_accessrules_rule) >= 0
INSERT INTO core_datastore (entity_key, entity_value) SELECT 'core.plugins.status.myluteceaccessrules.version', '1.0.2' FROM core_admin_right WHERE id_right = 'ACCESSRULES_MANAGEMENT' AND NOT EXISTS (SELECT 1 FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.myluteceaccessrules.version') AND (SELECT COUNT(encodebackurl) FROM mylutece_accessrules_rule) >= 0;

-- changeset myluteceaccessrules:prerun-installed-version-1.0.1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(DISTINCT 1) FROM core_admin_right WHERE id_right = 'ACCESSRULES_MANAGEMENT' AND NOT EXISTS (SELECT 1 FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.myluteceaccessrules.version') AND (SELECT COUNT(*) FROM mylutece_accessrules_rule) >= 0
INSERT INTO core_datastore (entity_key, entity_value) SELECT 'core.plugins.status.myluteceaccessrules.version', '1.0.1' FROM core_admin_right WHERE id_right = 'ACCESSRULES_MANAGEMENT' AND NOT EXISTS (SELECT 1 FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.myluteceaccessrules.version') AND (SELECT COUNT(*) FROM mylutece_accessrules_rule) >= 0;
