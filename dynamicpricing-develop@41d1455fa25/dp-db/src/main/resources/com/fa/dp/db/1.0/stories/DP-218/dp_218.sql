

DELETE FROM `RA_TNT_APP_PARAMS` WHERE  `ATTR_KEY`='SS_PMI_HUBZU_QUERY';

SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;
INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CREATED_BY`, `CREATED_ON`) VALUES (UUID(), @APP_ID, 'SS_RR_QUERY', 'SELECT b.loan_id, a.client_code FROM ENT_REPOS.er_invs a, ENT_REPOS.er_loan_invs b WHERE a.inv_id=b.inv_id AND b.active_flag=1 AND loan_id IN (:idList)', 'SYSTEM', NOW());

