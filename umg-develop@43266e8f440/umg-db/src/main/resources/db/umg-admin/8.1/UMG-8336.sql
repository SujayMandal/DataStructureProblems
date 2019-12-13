use umg_admin;

update umg_admin.NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 5px;text-align: center;}th {background-color: #F5F5F5;}</style><body style="font-family: Calibri;">Hi,<br><br>Following error has been encountered during execution in $environment<ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul> Modelet Status at  $executionTime , Refer table: #set( $count = 1 )<TABLE><TR><TH>Sl no.</TH><TH>Modelet</TH><TH>Pool Name</TH><TH>Status</TH>#foreach( $modelet in $modeletList)</TR><TR><TD>$count</TD><TD>$modelet.host :$modelet.port /$modelet.rServePort </TD><TD>$modelet.poolName</TD><TD>$modelet.modeletStatus</TD></TR> #set( $count = $count + 1 ) #end</TABLE><br><br><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'RUNTIME FAILURE MAIL TEMPLATE' ;

update umg_admin.NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port/$rServePort has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'MODELET RESTART TEMPLATE' ;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), 'R_MODELET_STARTUP_SCRIPT_RJAVA', 'Y', 'system', UNIX_TIMESTAMP(NOW()), 'system', UNIX_TIMESTAMP(NOW()));

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), 'R_MODELET_STARTUP_SCRIPT_RSERVE', 'Y', 'system', UNIX_TIMESTAMP(NOW()), 'system', UNIX_TIMESTAMP(NOW()));

ALTER TABLE `SYSTEM_MODELETS` ADD COLUMN `R_SERVE_PORT` INT(10) NULL DEFAULT '0' AFTER `POOL_NAME` ;

ALTER TABLE `SYSTEM_MODELETS` ADD COLUMN `R_MODE` VARCHAR(10) NULL DEFAULT 'rJava' AFTER `R_SERVE_PORT`;

use Ocwen;

ALTER TABLE `UMG_RUNTIME_TRANSACTION` ADD COLUMN `R_SERVE_PORT` INT(10) NULL DEFAULT '0' AFTER `MODEL_EXEC_ENV_NAME`;

commit;   