

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_KEY` = 'ocn.maximum.asset.value' WHERE `ATTR_KEY` = 'maximum.asset.value';
UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_KEY` = 'ocn.minimum.asset.value' WHERE `ATTR_KEY` = 'minimum.asset.value';

SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;
INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`) VALUES (UUID(), @APP_ID, 'nrz.maximum.asset.value', '750000', 'NRZ', 'SYSTEM', NOW()); 
INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`) VALUES (UUID(), @APP_ID, 'nrz.minimum.asset.value', '0', 'NRZ', 'SYSTEM', NOW());