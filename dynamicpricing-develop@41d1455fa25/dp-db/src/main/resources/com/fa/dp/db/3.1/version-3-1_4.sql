UPDATE WEEKN_DAILY_RUN_STATUS set LAST_RUN_DATE = DATE_FORMAT(STR_TO_DATE(SUBSTR(LAST_RUN_DATE, 1, 9), "%d-%M-%Y"),"%Y-%m-%d");

ALTER TABLE `WEEKN_DAILY_RUN_STATUS`
	CHANGE COLUMN `LAST_RUN_DATE` `LAST_RUN_DATE` DATETIME NULL DEFAULT NULL;

UPDATE DP_WEEKN_PARAMS SET MOST_RECENT_LIST_END_DATE = CONCAT(SUBSTR(MOST_RECENT_LIST_END_DATE,7,4),'-',SUBSTR(MOST_RECENT_LIST_END_DATE,1,2),'-',SUBSTR(MOST_RECENT_LIST_END_DATE,4,2)) WHERE MOST_RECENT_LIST_END_DATE LIKE('%/%');


SET @CREATED_BY = 'SYSTEM';

SET @CREATED_ON =  UNIX_TIMESTAMP() * 1000;

SET @PROCESS = 'QA_REPORT';

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), 'qaReportFetchData', 'QA Report Fetch Data', 1, @PROCESS, TRUE, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), 'qaReportAssignmentFilter', 'QA Report assignment filter', 2, @PROCESS, TRUE, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), 'qaReportPast12CyclesFilter', 'QA Report Past 12 cycle filter', 3, @PROCESS, TRUE, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), 'qaReportStateFilter', 'QA Report State filter', 4, @PROCESS, TRUE, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(), 'qaReportSSPmiFilter', 'QA Report SS PMI filter', 5, @PROCESS, TRUE, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT OT.SELR_PROP_ID_VC_NN, RBID_PROP_ID_VC_FK, LIST_END_DATE_DT_NN FROM RRRBPMTX_PROP_LIST, RRRBPMMS_PROP OT WHERE (RBID_PROP_ID_VC_FK, TO_DATE(LIST_STRT_DATE_DT_NN)) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_DATE(LIST_STRT_DATE_DT_NN)) FROM RRRBPMTX_PROP_LIST WHERE TO_DATE(LIST_END_DATE_DT_NN) > TO_DATE(?) AND TO_DATE(LIST_END_DATE_DT_NN)  <  TO_DATE(?) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) GROUP BY RBID_PROP_ID_VC_FK) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) AND LIST_TYPE_ID_VC_FK = \'AUCN\' AND OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND RBID_PROP_ID_VC_FK = OT.RBID_PROP_ID_VC_PK' WHERE `ATTR_KEY` = 'INITIAL_HUBZU_QUERY';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT T1.RBID_PROP_ID_VC_FK, T1.AUTO_RLST_VC, T1.OCCPNCY_STTS_AT_LST_CREATN, T1.RBID_PROP_LIST_ID_VC_PK, T1.LIST_STTS_DTLS_VC, T1.LIST_PRCE_NT, T1.LIST_STRT_DATE_DT_NN, T1.LIST_END_DATE_DT_NN, OT1.SELR_PROP_ID_VC_NN, OT1.PROP_STAT_ID_VC_FK, OT1.PROP_ZIP_VC_FK, OT1.PROP_STTS_ID_VC_FK FROM RRRBPMTX_PROP_LIST T1, RRRBPMMS_PROP OT1 WHERE T1.RBID_PROP_ID_VC_FK  IN (:idList) AND T1.LIST_END_DATE_DT_NN < TO_DATE(:endDate) AND T1.RBID_PROP_ID_VC_FK = OT1.RBID_PROP_ID_VC_PK AND T1.LIST_TYPE_ID_VC_FK = \'AUCN\' AND T1.OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND (T1.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR T1.LIST_STTS_DTLS_VC IS NULL) AND OT1.SELR_ACNT_ID_VC_FK IN (\'000\',\'900\',\'891\') AND OT1.PROP_SOLD_DATE_DT IS NULL AND (SELECT COUNT(*) FROM RRRBPMMS_PROP OT2 WHERE OT2.SELR_PROP_ID_VC_NN = OT1.SELR_PROP_ID_VC_NN AND OT1.PROP_SOLD_DATE_DT IS NULL AND OT2.PROP_SOLD_DATE_DT IS NOT NULL)=0 ORDER BY OT1.SELR_PROP_ID_VC_NN DESC, TO_DATE(LIST_STRT_DATE_DT_NN) DESC, T1.LIST_END_DATE_DT_NN DESC' WHERE `ATTR_KEY` = 'INITIAL_HUBZU_QUERY_ALL_ROWS';
