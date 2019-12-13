

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT c.PROP_TEMP AS loan_id, a.client_code FROM ENT_REPOS.er_invs a, ENT_REPOS.er_loan_invs b, ENT_REPOS.ER_PROPS c WHERE a.inv_id = b.inv_id AND b.active_flag=1 AND b.loan_id = c.LOAN_TEMP AND c.PROP_TEMP IN (:idList)' WHERE  `ATTR_KEY`='SS_RR_QUERY';
