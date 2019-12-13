UPDATE RA_TNT_APP_PARAMS SET `ATTR_VALUE`='SELECT T1.RBID_PROP_ID_VC_FK, OT1.SELR_ACNT_ID_VC_FK, T1.OCCPNCY_STTS_AT_LST_CREATN, T1.RBID_PROP_LIST_ID_VC_PK, T1.LIST_STTS_DTLS_VC, T1.LIST_PRCE_NT, T1.LIST_STRT_DATE_DT_NN, T1.LIST_END_DATE_DT_NN, T1.LIST_TYPE_ID_VC_FK, OT1.SELR_PROP_ID_VC_NN, OT1.PROP_STAT_ID_VC_FK, OT1.PROP_ZIP_VC_FK FROM RRRBPMTX_PROP_LIST T1, RRRBPMMS_PROP OT1 WHERE T1.RBID_PROP_ID_VC_FK IN (:idList) AND T1.RBID_PROP_ID_VC_FK = OT1.RBID_PROP_ID_VC_PK AND (T1.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR T1.LIST_STTS_DTLS_VC IS NULL) AND OT1.SELR_ACNT_ID_VC_FK IN (\'000\',\'900\',\'891\') ORDER BY TO_DATE(LIST_STRT_DATE_DT_NN) DESC, T1.LIST_END_DATE_DT_NN DESC' WHERE ATTR_KEY='FUTURE_REDUCTION_SEARCH_QUERY';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'INITIAL_HUBZU_QUERY_ALL_ROWS_PRIOR_RECOMMENDATION';