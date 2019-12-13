
SELECT ID from `RA_TNT_APPS` WHERE `NAME` = 'DPA' INTO @APP_ID;

SET @CREATED_BY = 'SYSTEM';

SET @CREATED_ON =  UNIX_TIMESTAMP() * 1000;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.email.body', 'Please find the summary of Dynamic Pricing QA report run on {0}.<br/><br/><table style="border: 1px solid #c1c7d0;text-align: center;border-spacing: 0px;"><tr style="background:aliceblue"><th style="border: 1px solid #c1c7d0"><b>Total Count</b></th><th style="border: 1px solid #c1c7d0"><b>Success Count</b></th><th style="border: 1px solid #c1c7d0"><b>Failure Count</b></th><th style="border: 1px solid #c1c7d0"><b>Run date</b></th></tr><tr><td style="border: 1px solid #c1c7d0;">{1}</td><td style="border: 1px solid #c1c7d0;">{2}</td><td style="border: 1px solid #c1c7d0;">{3}</td><td style="border: 1px solid #c1c7d0;">{4}</td></tr><table><br/>List of Failed Loan numbers:<br/>{5}<br/><br/>Thanks,<br/>REALAnalyticsSupport', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.email.tolist', 'saurabh.agarwal@altisource.com;realanalyticssupport@altisource.com', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.email.cclist', '', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.email.subject', 'Dynamic Pricing Daily QA Report - {0}', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES (UUID(), @APP_ID, 'qa.report.email.from', 'realanalyticssupport@altisource.com', NULL, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
