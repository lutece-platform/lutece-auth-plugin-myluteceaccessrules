-- liquibase formatted sql
-- changeset myluteceaccessrules:update_db_accessrules-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE mylutece_accessrules_rule add COLUMN encodebackurl SMALLINT;