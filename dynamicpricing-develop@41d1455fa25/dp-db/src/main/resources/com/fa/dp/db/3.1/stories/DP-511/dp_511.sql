
SELECT ID from `RA_TNT_APPS` WHERE `NAME` = 'DPA' INTO @APP_ID;

SET @CREATED_BY = 'SYSTEM';

SET @CREATED_ON =  UNIX_TIMESTAMP() * 1000;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.error.email.body', 'Please find the error summary of Dynamic Pricing QA report run on {0}.<br/><br/>{1}<br/><br/>Thanks,<br/>REALAnalyticsSupport', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
