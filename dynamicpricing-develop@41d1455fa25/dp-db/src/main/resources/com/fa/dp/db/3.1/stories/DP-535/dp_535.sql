UPDATE DP_WEEKN_PARAMS SET MOST_RECENT_LIST_END_DATE = CONCAT(SUBSTR(MOST_RECENT_LIST_END_DATE,7,4),'-',SUBSTR(MOST_RECENT_LIST_END_DATE,1,2),'-',SUBSTR(MOST_RECENT_LIST_END_DATE,4,2)) WHERE MOST_RECENT_LIST_END_DATE LIKE('%/%');