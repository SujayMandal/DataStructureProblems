

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'nrz.maximum.asset.value';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'nrz.minimum.asset.value';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_KEY` = 'maximum.asset.value' WHERE `ATTR_KEY` = 'ocn.maximum.asset.value';
UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_KEY` = 'minimum.asset.value' WHERE `ATTR_KEY` = 'ocn.minimum.asset.value';