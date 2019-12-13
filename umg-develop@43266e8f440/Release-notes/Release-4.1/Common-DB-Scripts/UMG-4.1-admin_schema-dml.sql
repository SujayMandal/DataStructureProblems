use umg_admin;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('cab8f2ef-8ce4-11e5-bf86-00ffde411c75', 'TRAN_DSHBRD_SELECTN_RECRD_CNT_LIMIT', 'count to set the max number of records allowed to be selected for download', '100', 'Y', 'system', 1417439330020, 'system', 1417439330020);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('89f880df-8de0-11e5-927e-00ffde411c75', 'BATCH_DSHBRD_SELECTN_RECRD_CNT_LIMIT', 'count to set the max number of records allowed to be selected for batch download', '5', 'Y', 'system', 1417439330020, 'system', 1417439330020);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('cab8f2ef-8ce4-11e5-bf86-00ffde411c99', 'STRINGS_AS_FACTORS', 'Boolean flag for stringsAsFactors', 'false', 'Y', 'system', 1417439330020, 'system', 1417439330020);

COMMIT;