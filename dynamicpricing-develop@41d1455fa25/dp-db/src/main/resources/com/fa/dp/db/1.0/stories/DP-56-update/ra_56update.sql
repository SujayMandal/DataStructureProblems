

SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;
INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CREATED_BY`, `CREATED_ON`) VALUES 
(UUID(), @APP_ID, 'nrz.weekNVacant.model.name', 'hubzuweekn', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekNVacant.major.version', '11', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekNVacant.minor.version', '0', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekN.dp.auth.token', 'iEVEc/pAi1mZFt9JLNfHkmwwkAizSxCXjf151NbkLxSR8sX2FnTeOEVR2fQ8Cj7Z', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'nrz.weekNVacant.tenant.code', 'hubzu', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNVacant.model.name', 'hubzuweekn', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNVacant.major.version', '11', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNVacant.minor.version', '0', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekN.dp.auth.token', 'iEVEc/pAi1mZFt9JLNfHkmwwkAizSxCXjf151NbkLxSR8sX2FnTeOEVR2fQ8Cj7Z', 'SYSTEM', NOW()),
(UUID(), @APP_ID, 'ocn.weekNVacant.tenant.code', 'hubzu', 'SYSTEM', NOW());








