DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RR_MIGRATION_PROP_TEMP_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RR_MIGRATION_LOAN_NUM_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RR_MIGRATION_OLD_RR_LOAN_NUM_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY';

DROP TABLE IF EXISTS `DP_MIGRATION_MAP`;

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT RBID_PROP_ID_VC_FK FROM RRRBPMTX_PROP_LIST, RRRBPMMS_PROP OT WHERE (RBID_PROP_ID_VC_FK, TO_DATE(LIST_STRT_DATE_DT_NN)) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_DATE(LIST_STRT_DATE_DT_NN)) FROM RRRBPMTX_PROP_LIST WHERE TO_DATE(LIST_STRT_DATE_DT_NN) BETWEEN TO_DATE(?) AND TO_DATE(?) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) GROUP BY RBID_PROP_ID_VC_FK) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) AND LIST_TYPE_ID_VC_FK = \'AUCN\' AND OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND TO_DATE(LIST_END_DATE_DT_NN) < TO_DATE(?) AND RBID_PROP_ID_VC_FK = OT.RBID_PROP_ID_VC_PK AND OT.PROP_STTS_ID_VC_FK != \'DELETED\'' WHERE  `ATTR_KEY`='INITIAL_HUBZU_QUERY';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT LOAN_NUM, AS_OF_DT,  FC_INVENTORY_FLAG,  NRZ_ACQUISITION_DT, SPECIAL_SERVICING_FLAG, PMI_FLAG, PROP_TEMP, OLD_RR_LOAN_NUM, CLIENT_ID  FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE PROP_TEMP = ? ORDER BY AS_OF_DT DESC FETCH FIRST 1 ROW ONLY' WHERE `ATTR_KEY` = 'RR_CLASSIFICATION_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RR_MIGRATION_QUERY';

ALTER TABLE `DP_WEEKN_PARAMS` DROP COLUMN `PROP_TEMP`;

ALTER TABLE `DP_WEEKN_PARAMS_ORIGINAL` DROP COLUMN `PROP_TEMP`;

ALTER TABLE `DP_WEEK0_PARAMS` DROP COLUMN `PROP_TEMP`;

ALTER TABLE `DP_WEEK0_PARAMS_ORIGINAL` DROP COLUMN `PROP_TEMP`;

ALTER TABLE `DP_WEEKN_AUDIT_REPORTS` DROP COLUMN `PROP_TEMP`;

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT a.loannumber , a.PROPSTRNBR, a.PROPSTREET, a.PROPCITY, a.STATE, a.PROPZIP, a.PROPTYPE, a.ORGPRINBAL, a.CURPRINBAL, a.PIPMTAMT, a.ESCROWPMT, a.OCCPTYPE, a.FAIRMKTVAL, a.CREDITSCORE, a.PURCHASEPRICE, a.CREDITSCOREDT, a.ORGAPPRVAL, b.referral_dt as REO_DATE, c.upb_res FROM arlt_arlt_mortgage1 a left outer join REO_ER_PROPS b on a.loannumber = b.LOAN_TEMP left outer join SHAHMAYU.REO_SALES_MASTER c on a.loannumber = c.loan_num WHERE a.loannumber = ?' WHERE  `ATTR_KEY`='RR_QUERY';