SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CREATED_BY`, `CREATED_ON`) VALUES (UUID(), @APP_ID, 'sop.weekn.prior.recommandation.all.rows.hubzu.query', 'SELECT T1.RBID_PROP_ID_VC_FK, T1.AUTO_RLST_VC, T1.OCCPNCY_STTS_AT_LST_CREATN, T1.RBID_PROP_LIST_ID_VC_PK, T1.LIST_STTS_DTLS_VC, T1.LIST_PRCE_NT, T1.LIST_STRT_DATE_DT_NN, T1.LIST_END_DATE_DT_NN, OT1.SELR_PROP_ID_VC_NN, OT1.PROP_STAT_ID_VC_FK, OT1.PROP_ZIP_VC_FK, OT1.PROP_STTS_ID_VC_FK FROM RRRBPMTX_PROP_LIST T1, RRRBPMMS_PROP OT1 WHERE T1.RBID_PROP_ID_VC_FK  IN (:idList) AND T1.RBID_PROP_ID_VC_FK = OT1.RBID_PROP_ID_VC_PK AND T1.LIST_TYPE_ID_VC_FK = \'AUCN\' AND T1.OCCPNCY_STTS_AT_LST_CREATN <> \'N\' AND (T1.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR T1.LIST_STTS_DTLS_VC IS NULL) AND OT1.SELR_ACNT_ID_VC_FK IN (\'000\',\'900\',\'891\') AND OT1.PROP_SOLD_DATE_DT IS NULL AND (SELECT COUNT(*) FROM RRRBPMMS_PROP OT2 WHERE OT2.SELR_PROP_ID_VC_NN = OT1.SELR_PROP_ID_VC_NN AND OT1.PROP_SOLD_DATE_DT IS NULL AND OT2.PROP_SOLD_DATE_DT IS NOT NULL)=0 ORDER BY OT1.SELR_PROP_ID_VC_NN DESC, TO_DATE(LIST_STRT_DATE_DT_NN) DESC, T1.LIST_END_DATE_DT_NN DESC', 'SYSTEM', NOW());