use umg_admin;


INSERT INTO POOL (`ID`, `POOL_NAME`, `POOL_DESCRIPTION`, `IS_DEFAULT_POOL`, `ENVIRONMENT`, `MODELET_COUNT`, `MODELET_CAPACITY`, `PRIORITY`, `WAIT_TIMEOUT`) 
	VALUES ('100', 'RENT_RANGE_ONLINE', 'RENT_RANGE_ONLINE', '0', 'R', '1', '4GB - Linux 64 bit', '7', '0');

INSERT INTO POOL_CRITERIA_DEF_MAPPING (`ID`, `POOL_ID`, `POOL_CRITERIA_VALUE`) VALUES ('100', '100', '#TENANT# = any & #ENVIRONMENT# = R & #ENVIRONMENT_VERSION# = 3.2.1 & #TRANSACTION_TYPE# = ONLINE & #MODEL# = ANY & #MODEL_VERSION# = ANY & #TRANSACTION_MODE# = ANY');

INSERT INTO POOL_USAGE_ORDER (`ID`, `POOL_ID`, `POOL_USAGE_ID`, `POOL_TRY_ORDER`) VALUES ('100', '100', '100', '1');




INSERT INTO POOL (`ID`, `POOL_NAME`, `POOL_DESCRIPTION`, `IS_DEFAULT_POOL`, `ENVIRONMENT`, `MODELET_COUNT`, `MODELET_CAPACITY`, `PRIORITY`, `WAIT_TIMEOUT`) 
	VALUES ('101', 'RENT_RANGE_BULK', 'RENT_RANGE_BULK', '0', 'R', '1', '4GB - Linux 64 bit', '8', '0');

INSERT INTO POOL_CRITERIA_DEF_MAPPING (`ID`, `POOL_ID`, `POOL_CRITERIA_VALUE`) VALUES ('101', '101', '#TENANT# = any & #ENVIRONMENT# = R & #ENVIRONMENT_VERSION# = 3.2.1 & #TRANSACTION_TYPE# = BULK & #MODEL# = ANY & #MODEL_VERSION# = ANY & #TRANSACTION_MODE# = ANY');

INSERT INTO POOL_USAGE_ORDER (`ID`, `POOL_ID`, `POOL_USAGE_ID`, `POOL_TRY_ORDER`) VALUES ('101', '101', '101', '1');

INSERT INTO POOL_USAGE_ORDER (`ID`, `POOL_ID`, `POOL_USAGE_ID`, `POOL_TRY_ORDER`) VALUES ('102', '101', '100', '2');


commit;