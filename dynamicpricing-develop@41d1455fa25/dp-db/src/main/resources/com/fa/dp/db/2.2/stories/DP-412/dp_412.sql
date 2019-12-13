

SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), @APP_ID, 'RR_MIGRATION_PROP_TEMP_QUERY', 'SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT FROM (SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT, MAX(AS_OF_DT) OVER (PARTITION BY PROP_TEMP) AS LATEST_DATE FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE PROP_TEMP IN (:idList)) WHERE AS_OF_DT = LATEST_DATE', NULL, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), @APP_ID, 'RR_MIGRATION_LOAN_NUM_QUERY', 'SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT FROM (SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT, MAX(AS_OF_DT) OVER (PARTITION BY LOAN_NUM) AS LATEST_DATE FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE LOAN_NUM IN (:idList) AND PROP_TEMP IS NOT NULL) WHERE AS_OF_DT = LATEST_DATE', NULL, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), @APP_ID, 'RR_MIGRATION_OLD_RR_LOAN_NUM_QUERY', 'SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT FROM (SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT, MAX(AS_OF_DT) OVER (PARTITION BY OLD_RR_LOAN_NUM) AS LATEST_DATE FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE OLD_RR_LOAN_NUM IN (:idList)) WHERE AS_OF_DT = LATEST_DATE', NULL, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), @APP_ID, 'RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY', 'SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT FROM (SELECT LOAN_NUM, OLD_RR_LOAN_NUM, PROP_TEMP, AS_OF_DT, MAX(AS_OF_DT) OVER (PARTITION BY LOAN_NUM) AS LATEST_DATE FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE LOAN_NUM IN (:idList) AND OLD_RR_LOAN_NUM IS NULL AND PROP_TEMP IS NOT NULL) WHERE AS_OF_DT = LATEST_DATE', NULL, 'SYSTEM', NOW(), 'SYSTEM', NOW());

DROP TABLE IF EXISTS `DP_MIGRATION_MAP`;
CREATE TABLE IF NOT EXISTS `DP_MIGRATION_MAP` (
  `ID` char(36) NOT NULL,
  `ASSET_NUMBER` char(36) NOT NULL,
  `OLD_ASSET_NUMBER` char(36),
  `PROP_TEMP` char(36) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY PROP_TEMP (PROP_TEMP)
  ) ENGINE=InnoDB;
  
UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT OT.SELR_PROP_ID_VC_NN, RBID_PROP_ID_VC_FK, LIST_END_DATE_DT_NN FROM RRRBPMTX_PROP_LIST, RRRBPMMS_PROP OT WHERE (RBID_PROP_ID_VC_FK, TO_DATE(LIST_STRT_DATE_DT_NN)) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_DATE(LIST_STRT_DATE_DT_NN)) FROM RRRBPMTX_PROP_LIST WHERE TO_DATE(LIST_STRT_DATE_DT_NN) BETWEEN TO_DATE(?) AND TO_DATE(?) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) GROUP BY RBID_PROP_ID_VC_FK) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) AND LIST_TYPE_ID_VC_FK = \'AUCN\' AND OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND RBID_PROP_ID_VC_FK = OT.RBID_PROP_ID_VC_PK' WHERE  `ATTR_KEY`='INITIAL_HUBZU_QUERY';