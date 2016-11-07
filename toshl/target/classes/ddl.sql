DROP TABLE `bill`;

CREATE TABLE `bill` (
  `ID` varchar(16) NOT NULL,
  `SRC` varchar(128) DEFAULT NULL,
  `GMT` varchar(11) NOT NULL,
  `AMOUNT` bigint(18) NOT NULL COMMENT 'in CN, cent',
  `AMOUNT_DESP` varchar(128) NOT NULL COMMENT 'in CN, cent',
  `TYPE` varchar(128) NOT NULL COMMENT 'tag start with letter',
  `EVENT` varchar(128) DEFAULT NULL COMMENT 'tag start with digit',
  `SUB_TYPE` varchar(128) DEFAULT NULL,
  `INFO` varchar(256) DEFAULT NULL,
  `EXT_INFO` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
