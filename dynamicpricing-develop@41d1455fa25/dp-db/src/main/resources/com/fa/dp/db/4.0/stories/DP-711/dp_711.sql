SELECT ID from `RA_TNT_APPS` WHERE LOWER(code) = 'dpa' INTO @APP_ID;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CREATED_BY`, `CREATED_ON`) VALUES 
(UUID(), @APP_ID, 'nrz.weekNSOP.model.name', 'NRZweekN', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekNSOP.major.version', '1', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekNSOP.minor.version', '', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'phh.weekNSOP.model.name', 'hubzuweekn', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'phh.weekNSOP.major.version', '1', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'phh.weekNSOP.minor.version', '', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNSOP.model.name', 'hubzuweekn', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNSOP.major.version', '1', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNSOP.minor.version', '', 'SYSTEM', NOW());