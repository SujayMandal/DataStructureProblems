USE umg_admin;

SET @CREATED_ON =  UNIX_TIMESTAMP();
SET @PATH = "/opt/raconf";
 
 INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ( UUID() , 'largeFilesFolder', 'largesupportpackage', 'Y', 'system', @CREATED_ON , 'system', @CREATED_ON);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ( UUID() , 'IndexFileTempPath', @PATH, 'Y', 'system', @CREATED_ON , 'system', @CREATED_ON);
commit;
		