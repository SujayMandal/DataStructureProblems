use umg_admin;

UPDATE NOTIFICATION_EMAIL_TEMPLATE SET BODY_DEFINITION='<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' WHERE  NAME='MODELET RESTART TEMPLATE';

INSERT INTO `SYSTEM_KEY` (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(),'ModelOutput_Validation','TENANT','SYSTEM',UNIX_TIMESTAMP(),'SYSTEM',UNIX_TIMESTAMP());

SELECT @maxConnectionAgeId := ID FROM SYSTEM_KEY WHERE SYSTEM_KEY= 'maxConnectionAge';

SELECT @maxIdleTimeId := ID FROM SYSTEM_KEY WHERE SYSTEM_KEY= 'maxIdleTime';

UPDATE TENANT_CONFIG SET CONFIG_VALUE=0 WHERE  ID= @maxConnectionAgeId;

UPDATE TENANT_CONFIG SET CONFIG_VALUE=28200 WHERE  ID= @maxIdleTimeId;

INSERT INTO POOL_CRITERIA VALUES (8, 'CHANNEL', 8);

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_DEF_MAPPING.POOL_CRITERIA_VALUE = CONCAT(POOL_CRITERIA_DEF_MAPPING.POOL_CRITERIA_VALUE, ' & #CHANNEL# = Any');


SELECT @criteria_priority_type:= CRITERIA_PRIORITY FROM POOL_CRITERIA WHERE CRITERIA_NAME='TRANSACTION_TYPE';

SELECT @criteria_priority_mode:= CRITERIA_PRIORITY FROM POOL_CRITERIA WHERE CRITERIA_NAME='TRANSACTION_MODE';

UPDATE POOL_CRITERIA SET CRITERIA_PRIORITY=@criteria_priority_type WHERE CRITERIA_NAME='TRANSACTION_MODE';

UPDATE POOL_CRITERIA SET CRITERIA_PRIORITY=@criteria_priority_mode WHERE  CRITERIA_NAME= 'TRANSACTION_TYPE';

commit;

UPDATE `SYSTEM_KEY` SET `SYSTEM_KEY`='RUNTIME_BASE_URL' WHERE `SYSTEM_KEY`='URL' AND `KEY_TYPE`='TENANT';

commit;