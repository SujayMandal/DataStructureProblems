

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT b.loan_id, a.client_code FROM ENT_REPOS.er_invs a, ENT_REPOS.er_loan_invs b WHERE a.inv_id=b.inv_id AND b.active_flag=1 AND loan_id IN (:idList)' WHERE  `ATTR_KEY`='SS_RR_QUERY';
