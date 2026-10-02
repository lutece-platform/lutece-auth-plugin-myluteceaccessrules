-- liquibase formatted sql
--
-- Precondition : the rules table does not exist yet. The query fails while it exists (MARK_RAN : no DROP
-- TABLE on an installed plugin) and errors when it does not (WARN : the changeset runs).
--
-- changeset myluteceaccessrules:create_db_accessrules.sql logicalFilePath:sql/plugins/accessrules/plugin/create_db_accessrules.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM mylutece_accessrules_rule WHERE 1 = 0

--
-- Structure for table mylutece_accessrules_rule
--

DROP TABLE IF EXISTS mylutece_accessrules_rule;
CREATE TABLE mylutece_accessrules_rule (
id_rule int AUTO_INCREMENT,
title varchar(50) default '' NOT NULL,
description varchar(255) default '',
enable SMALLINT,
external SMALLINT,
messagetodisplay long varchar,
redirecturl varchar(255) default '',
backurl varchar(255) default '',
priority_order int default 0 ,
encodebackurl SMALLINT,

PRIMARY KEY (id_rule)
);
