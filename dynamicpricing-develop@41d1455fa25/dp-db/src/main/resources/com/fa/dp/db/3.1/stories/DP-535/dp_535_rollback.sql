UPDATE DP_WEEKN_PARAMS SET MOST_RECENT_LIST_END_DATE = CONCAT(SUBSTR(MOST_RECENT_LIST_END_DATE,6,2),'/',SUBSTR(MOST_RECENT_LIST_END_DATE,9,2),'/',SUBSTR(MOST_RECENT_LIST_END_DATE,1,4)) WHERE FAILED_STEP_COMMAND_ID IS NOT NULL;