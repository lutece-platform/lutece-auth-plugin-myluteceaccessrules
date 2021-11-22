
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
PRIMARY KEY (id_rule)
);
