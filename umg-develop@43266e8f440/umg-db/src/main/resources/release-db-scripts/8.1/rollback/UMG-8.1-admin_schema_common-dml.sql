USE umg_admin;

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = CONCAT(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 3.2.1') WHERE POOL_ID=(SELECT ID FROM POOL WHERE EXECUTION_LANGUAGE='R');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = CONCAT(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 2013') WHERE POOL_ID=(SELECT ID FROM POOL WHERE EXECUTION_LANGUAGE='Excel');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = CONCAT(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 7.16') WHERE POOL_ID=(SELECT ID FROM POOL WHERE EXECUTION_LANGUAGE='Matlab');

DELETE FROM `MODEL_EXECUTION_ENVIRONMENTS` WHERE NAME = 'R-3.3.2';

INSERT INTO `POOL_CRITERIA` (`ID`, `CRITERIA_NAME`, `CRITERIA_PRIORITY`) VALUES ('3', 'EXECUTION_LANGUAGE_VERSION ', 2);
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=3 WHERE  `CRITERIA_NAME`='EXECUTION_ENVIRONMENT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=4 WHERE  `CRITERIA_NAME`='MODEL';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=5 WHERE  `CRITERIA_NAME`='MODEL_VERSION';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=6 WHERE  `CRITERIA_NAME`='TENANT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=7 WHERE  `CRITERIA_NAME`='TRANSACTION_MODE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=8 WHERE  `CRITERIA_NAME`='TRANSACTION_TYPE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=9 WHERE  `CRITERIA_NAME`='CHANNEL';


update NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 5px;text-align: center;}th {background-color: #F5F5F5;}</style><body style="font-family: Calibri;">Hi,<br><br>Following error has been encountered during execution in $environment<ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul> Modelet Status at  $executionTime , Refer table: #set( $count = 1 )<TABLE><TR><TH>Sl no.</TH><TH>Modelet</TH><TH>Pool Name</TH><TH>Status</TH>#foreach( $modelet in $modeletList)</TR><TR><TD>$count</TD><TD>$modelet.host :$modelet.port </TD><TD>$modelet.poolName</TD><TD>$modelet.modeletStatus</TD></TR> #set( $count = $count + 1 ) #end</TABLE><br><br><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'RUNTIME FAILURE MAIL TEMPLATE' ;

update NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'MODELET RESTART TEMPLATE' ;

DELETE FROM `SYSTEM_PARAMETER`  WHERE SYS_KEY = 'R_MODELET_STARTUP_SCRIPT_RJAVA';

DELETE FROM `SYSTEM_PARAMETER` WHERE SYS_KEY = 'R_MODELET_STARTUP_SCRIPT_RSERVE';

DELETE FROM MODEL_EXEC_PACKAGES WHERE MODEL_EXEC_ENV_NAME = 'R-3.3.2';

commit;