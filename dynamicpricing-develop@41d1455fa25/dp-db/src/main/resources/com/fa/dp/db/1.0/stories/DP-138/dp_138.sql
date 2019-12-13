

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT T1.RBID_PROP_ID_VC_FK, T1.OCCPNCY_STTS_AT_LST_CREATN, T1.RBID_PROP_LIST_ID_VC_PK, T1.LIST_STTS_DTLS_VC, T1.LIST_PRCE_NT, T1.LIST_STRT_DATE_DT_NN, T1.LIST_END_DATE_DT_NN, OT1.SELR_PROP_ID_VC_NN, OT1.PROP_STAT_ID_VC_FK, OT1.PROP_ZIP_VC_FK FROM RRRBPMTX_PROP_LIST T1, RRRBPMMS_PROP OT1, (SELECT RBID_PROP_ID_VC_FK FROM RRRBPMTX_PROP_LIST WHERE (RBID_PROP_ID_VC_FK, TO_NUMBER(regexp_replace(RBID_PROP_LIST_ID_VC_PK,\'[[:alpha:]]\'))) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_NUMBER(regexp_replace(RBID_PROP_LIST_ID_VC_PK,\'[[:alpha:]]\'))) FROM RRRBPMTX_PROP_LIST WHERE RBID_PROP_ID_VC_FK IN (SELECT RBID_PROP_ID_VC_FK FROM RRRBPMTX_PROP_LIST WHERE (RBID_PROP_ID_VC_FK, TO_NUMBER(regexp_replace(RBID_PROP_LIST_ID_VC_PK,\'[[:alpha:]]\'))) IN (SELECT RBID_PROP_ID_VC_FK, MAX(TO_NUMBER(regexp_replace(RBID_PROP_LIST_ID_VC_PK,\'[[:alpha:]]\'))) FROM RRRBPMTX_PROP_LIST GROUP BY RBID_PROP_ID_VC_FK) AND LIST_TYPE_ID_VC_FK != \'TRNL\') AND LIST_STRT_DATE_DT_NN BETWEEN TO_DATE(?) AND TO_DATE(?) GROUP BY RBID_PROP_ID_VC_FK) AND LIST_TYPE_ID_VC_FK = \'AUCN\' AND OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND (LIST_STTS_DTLS_VC NOT IN (\'SUCCESSFUL\',\'UNDERREVIEW\') OR LIST_STTS_DTLS_VC IS NULL)) T2 WHERE T1.RBID_PROP_ID_VC_FK = T2.RBID_PROP_ID_VC_FK AND T1.LIST_TYPE_ID_VC_FK = \'AUCN\' AND T1.OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND T1.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') AND T1.RBID_PROP_ID_VC_FK = OT1.RBID_PROP_ID_VC_PK AND OT1.SELR_ACNT_ID_VC_FK in (\'000\',\'900\')   ORDER BY T1.RBID_PROP_ID_VC_FK DESC, TO_NUMBER(regexp_replace(T1.RBID_PROP_LIST_ID_VC_PK,\'[[:alpha:]]\')) DESC, T1.LIST_END_DATE_DT_NN DESC' WHERE `ATTR_KEY` = 'INITIAL_HUBZU_QUERY';