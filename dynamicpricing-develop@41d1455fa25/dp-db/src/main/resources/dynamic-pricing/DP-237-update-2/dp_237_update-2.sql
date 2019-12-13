

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT T1.RBID_PROP_ID_VC_FK, T1.AUTO_RLST_VC, T1.OCCPNCY_STTS_AT_LST_CREATN, T1.RBID_PROP_LIST_ID_VC_PK, T1.LIST_STTS_DTLS_VC, T1.LIST_PRCE_NT, T1.LIST_STRT_DATE_DT_NN, T1.LIST_END_DATE_DT_NN, OT1.SELR_PROP_ID_VC_NN, OT1.PROP_STAT_ID_VC_FK, OT1.PROP_ZIP_VC_FK FROM RRRBPMTX_PROP_LIST T1, RRRBPMMS_PROP OT1 WHERE T1.RBID_PROP_ID_VC_FK  IN (:idList) AND T1.RBID_PROP_ID_VC_FK = OT1.RBID_PROP_ID_VC_PK AND T1.LIST_TYPE_ID_VC_FK = \'AUCN\' AND T1.OCCPNCY_STTS_AT_LST_CREATN <> \'Y\' AND (T1.LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\',\'DISAPPROVED\') OR T1.LIST_STTS_DTLS_VC IS NULL) AND OT1.SELR_ACNT_ID_VC_FK IN (\'000\',\'900\') AND OT1.PROP_SOLD_DATE_DT IS NULL AND (SELECT COUNT(*) FROM RRRBPMMS_PROP OT2 WHERE OT2.SELR_PROP_ID_VC_NN = OT1.SELR_PROP_ID_VC_NN AND OT1.PROP_SOLD_DATE_DT IS NULL AND OT2.PROP_SOLD_DATE_DT IS NOT NULL)=0 ORDER BY OT1.SELR_PROP_ID_VC_NN DESC, TO_DATE(LIST_STRT_DATE_DT_NN) DESC, T1.LIST_END_DATE_DT_NN DESC' WHERE  `ATTR_KEY`='INITIAL_HUBZU_QUERY_ALL_ROWS';

ALTER TABLE `RA_TNT_APP_PARAMS` CHANGE COLUMN `ATTR_VALUE` `ATTR_VALUE` VARCHAR(5000) NULL DEFAULT NULL AFTER `ATTR_KEY`;

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = 'SELECT * FROM ((SELECT SELR_PROP_ID_VC_NN,SELR_ACNT_ID_VC_FK,RBID_PROP_ID_VC_PK,PROP_STRT_NUMB_VC_NN|| \' \'|| PROP_STRT_NAME_VC_NN AS ADDRESS,PROP_CITY_VC_FK,PROP_STAT_ID_VC_FK,PROP_ZIP_VC_FK,PROP_CNTY_VC,PROP_SUB_TYPE_ID_VC_FK,AREA_SQUR_FEET_NM,LOT_SIZE_VC,BDRM_CNT_NT,BTRM_CNT_NM,TOTL_ROOM_CNT_NM,BULD_DATE_DT,REPR_VALU_NT,REO_PROP_STTS_VC,REO_DATE_DT,PROP_SOLD_DATE_DT,PROP_STTS_ID_VC_FK FROM RRRBPMMS_PROP WHERE RBID_PROP_ID_VC_PK IN (SELECT RBID_PROP_ID_VC_FK FROM RRRBPMTX_PROP_LIST WHERE LIST_STRT_DATE_DT_NN > \'01-JAN-2013\')) TBLS LEFT OUTER JOIN(SELECT TBLA.RBID_PROP_LIST_ID_VC_PK,TBLA.LIST_TYPE_ID_VC_FK,TBLA.RBID_PROP_ID_VC_FK,TBLA.LIST_ATMP_NUMB_NT_NN,TBLA.CURRENT_LIST_STRT_DATE,TBLA.CURRENT_LIST_END_DATE,TBLA.LIST_STTS_DTLS_VC,TBLA.PROPERTY_SOLD,TBLA.ACTV_AUTO_BID,TBLA.CURRENT_rsrv_prce_nt,TBLA.CURRENT_list_prce_nt,TBLA.HGST_BID_AMNT_NT,TBLA.MINM_BID_AMNT_NT,TBLA.OCCPNCY_STTS_AT_LST_CREATN,TBLA.SOP_PROGRAM_STATUS,TBLA.IS_STAT_HOT_VC,TBLA.BUY_IT_NOW_PRCE_NT,TBLA.RSRV_PRCE_MET_VC,TBLA.FALL_OUT_RESN_VC,TBLA.FALL_OUT_DATE_DT,TBLC.Financial_Considered_Indicator,TBLC.Cash_Only_Indicator,NVL(TBLB.BIDS, 0) AS PROP_BIDDING_NUMBIDS,NVL(TBLB.DISTINCT_BIDDERS, 0) AS PROP_BIDDING_DISTINCT_BIDDERS,TBLB.BID_AMNT_NT_NN AS PROP_BIDDING_MAX_BID,TBLB.BID_AMNT_NT_NN_2 AS PROP_BIDDING_MIN_BID,TBLD.TOTAL_NO_VIEWS,TBLE.PROP_BIDDING_DSTNCT_WTCHLST FROM ((SELECT a.RBID_PROP_LIST_ID_VC_PK,a.LIST_TYPE_ID_VC_FK,a.RBID_PROP_ID_VC_FK,a.LIST_ATMP_NUMB_NT_NN,a.LIST_STRT_DATE_DT_NN AS CURRENT_LIST_STRT_DATE,a.LIST_END_DATE_DT_NN AS CURRENT_LIST_END_DATE,a.LIST_STTS_DTLS_VC,(CASE WHEN a.LIST_STTS_DTLS_VC = \'SUCCESSFUL\' THEN \'Y\' ELSE \'N\' END)AS PROPERTY_SOLD,a.actv_auto_bid_limt_nt AS ACTV_AUTO_BID,a.rsrv_prce_nt AS CURRENT_rsrv_prce_nt,a.list_prce_nt AS CURRENT_list_prce_nt,a.HGST_BID_AMNT_NT,a.MINM_BID_AMNT_NT,a.OCCPNCY_STTS_AT_LST_CREATN,(CASE WHEN a.OCCPNCY_STTS_AT_LST_CREATN = \'Y\' THEN \'Y\' ELSE \'N\' END) AS  SOP_PROGRAM_STATUS,b.IS_STAT_HOT_VC,a.BUY_IT_NOW_PRCE_NT,a.RSRV_PRCE_MET_VC,a.FALL_OUT_RESN_VC,a.FALL_OUT_DATE_DT FROM RRRBPMTX_PROP_LIST a INNER JOIN  RRRBPMMS_PROP b ON a.RBID_PROP_ID_VC_FK= b.RBID_PROP_ID_VC_PK AND a.LIST_TYPE_ID_VC_FK = \'AUCN\' AND b.PROP_SOLD_DATE_DT IS NULL AND (SELECT COUNT(*) FROM RRRBPMMS_PROP OT2 WHERE OT2.SELR_PROP_ID_VC_NN = b.SELR_PROP_ID_VC_NN AND b.PROP_SOLD_DATE_DT IS NULL AND OT2.PROP_SOLD_DATE_DT IS NOT NULL)=0) TBLA LEFT OUTER JOIN( SELECT RBID_PROP_LIST_ID_VC_FK, COUNT(*) BIDS,COUNT(DISTINCT(B.SBMT_BY_VC_NN_FK)) DISTINCT_BIDDERS, MAX(B.BID_AMNT_NT_NN) BID_AMNT_NT_NN,MIN(B.BID_AMNT_NT_NN) BID_AMNT_NT_NN_2 FROM RRRBBMTX_BID B GROUP BY B.RBID_PROP_LIST_ID_VC_FK) TBLB ON TBLA.RBID_PROP_LIST_ID_VC_PK = TBLB.RBID_PROP_LIST_ID_VC_FK LEFT OUTER JOIN(SELECT DISTINCT a.RBID_PROP_LIST_ID_VC_FK,b.Financial_Considered_Indicator,c.Cash_Only_Indicator FROM RRRBBMTX_BID a LEFT OUTER JOIN (SELECT DISTINCT RBID_PROP_LIST_ID_VC_FK, (CASE WHEN PUCH_OPTN_VC = \'FINANCING\' THEN \'Y\' ELSE \'N\' END) AS Financial_Considered_Indicator FROM RRRBBMTX_BID ) b ON a.RBID_PROP_LIST_ID_VC_FK = b.RBID_PROP_LIST_ID_VC_FK AND b.Financial_Considered_Indicator = \'Y\' LEFT OUTER JOIN (SELECT DISTINCT RBID_PROP_LIST_ID_VC_FK, ( CASE WHEN PUCH_OPTN_VC = \'CASH\' THEN \'Y\' ELSE \'N\' END) AS Cash_Only_Indicator FROM RRRBBMTX_BID ) c ON a.RBID_PROP_LIST_ID_VC_FK = c.RBID_PROP_LIST_ID_VC_FK AND c.Cash_Only_Indicator= \'Y\' ) TBLC ON TBLA.RBID_PROP_LIST_ID_VC_PK = TBLC.RBID_PROP_LIST_ID_VC_FK LEFT OUTER JOIN ( SELECT RBID_PROP_LIST_ID_VC_FK, COUNT(*) TOTAL_NO_VIEWS FROM RRRBPMTX_USER_PROP_AUDT GROUP BY RBID_PROP_LIST_ID_VC_FK ) TBLD ON TBLA.RBID_PROP_LIST_ID_VC_PK = TBLD.RBID_PROP_LIST_ID_VC_FK LEFT OUTER JOIN ( SELECT RBID_PROP_LIST_ID_VC_PK, SUM(WTCHLST) AS PROP_BIDDING_DSTNCT_WTCHLST FROM (SELECT RBID_PROP_LIST_ID_VC_PK, ( CASE WHEN CRTD_DATE_DT_NN >= LIST_STRT_DATE_DT_NN AND CRTD_DATE_DT_NN<= LIST_END_DATE_DT_NN THEN 1 ELSE 0 END) AS WTCHLST FROM ( (SELECT RBID_PROP_ID_VC_FK, RBID_PROP_LIST_ID_VC_PK,LIST_STRT_DATE_DT_NN, LIST_END_DATE_DT_NN FROM RRRBPMTX_PROP_LIST WHERE LIST_TYPE_ID_VC_FK = \'AUCN\' ORDER BY RBID_PROP_LIST_ID_VC_PK ) a LEFT OUTER JOIN (SELECT RBID_PROP_ID_VC_PF, RBID_USER_ID_VC_PF, CRTD_DATE_DT_NN FROM RRRBUMMP_USER_WLST WHERE NTFC_TYPE_VC_NN = \'DAILY\' ) b ON a.RBID_PROP_ID_VC_FK=b.RBID_PROP_ID_VC_PF ) ) GROUP BY RBID_PROP_LIST_ID_VC_PK ) TBLE ON TBLA.RBID_PROP_LIST_ID_VC_PK = TBLE.RBID_PROP_LIST_ID_VC_PK ) ) TBLM ON TBLS.RBID_PROP_ID_VC_PK = TBLM.RBID_PROP_ID_VC_FK ) WHERE SELR_PROP_ID_VC_NN = ? AND (LIST_STTS_DTLS_VC NOT IN (\'CANCELLED\', \'DISAPPROVED\') OR LIST_STTS_DTLS_VC IS NULL) AND SOP_PROGRAM_STATUS <> \'Y\' ORDER BY CURRENT_LIST_STRT_DATE ASC' WHERE `ATTR_KEY` = 'HUBZU_QUERY';
