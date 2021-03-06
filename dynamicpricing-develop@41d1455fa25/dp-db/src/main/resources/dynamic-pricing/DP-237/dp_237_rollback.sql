

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT RBID_PROP_ID_VC_FK FROM RRRBPMTX_PROP_LIST WHERE (RBID_PROP_ID_VC_FK, TO_DATE(LIST_STRT_DATE_DT_NN)) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_DATE(LIST_STRT_DATE_DT_NN)) FROM RRRBPMTX_PROP_LIST WHERE TO_DATE(LIST_STRT_DATE_DT_NN) BETWEEN TO_DATE(?) AND TO_DATE(?) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) GROUP BY RBID_PROP_ID_VC_FK) AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') AND LIST_STTS_DTLS_VC NOT  IN (\'SUCCESSFUL\',\'UNDERREVIEW\') OR LIST_STTS_DTLS_VC IS NULL) AND LIST_TYPE_ID_VC_FK = \'AUCN\' AND OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND TO_DATE(LIST_END_DATE_DT_NN) < TO_DATE(?)'  WHERE  `ATTR_KEY`='INITIAL_HUBZU_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'SUCCESS_UNDERREVIEW_HUBZU_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'SUCCESS_UNDERREVIEW_INITAL_HUBZU_QUERY';

ALTER TABLE `DP_WEEKN_PARAMS` DROP COLUMN `PROP_SOLD_DATE_DT`;

DROP TABLE `DP_SOP_WEEKN_INTG_AUDITS`;

