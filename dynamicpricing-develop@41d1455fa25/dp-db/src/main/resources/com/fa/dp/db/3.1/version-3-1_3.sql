
UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT t1.SELR_ACNT_ID_VC_FK, t1.SELR_PROP_ID_VC_NN, t1.RBID_PROP_ID_VC_PK, t2.RBID_PROP_ID_VC_FK, t1.REO_PROP_STTS_VC , t1.PROP_SOLD_DATE_DT, t1.PROP_STTS_ID_VC_FK, t2.RBID_PROP_ID_VC_FK, t2.RBID_PROP_LIST_ID_VC_PK, t2.LIST_TYPE_ID_VC_FK, t2.LIST_PRCE_NT, t2.LIST_STRT_DATE_DT_NN, t2.LIST_END_DATE_DT_NN, t2.LIST_STTS_DTLS_VC, t2.OCCPNCY_STTS_AT_LST_CREATN FROM RRRBPMMS_PROP t1 JOIN RRRBPMTX_PROP_LIST t2 ON t1.RBID_PROP_ID_VC_PK = t2.RBID_PROP_ID_VC_FK WHERE TO_DATE(t2.LIST_END_DATE_DT_NN) >= TO_DATE(?) AND TO_DATE(t2.LIST_END_DATE_DT_NN) < TO_DATE(?) AND t2.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') AND t2.OCCPNCY_STTS_AT_LST_CREATN <> \'Y\'' WHERE `ATTR_KEY` = 'hubzu.daily.qa.report';

