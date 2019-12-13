USE umg_admin; 

 DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY`='largeFilesFolder';
 Delete from `SYSTEM_PARAMETER` where `SYS_KEY` = 'IndexFileTempPath';
 Delete from `SYSTEM_PARAMETER` where `SYS_KEY` = 'ALLOWED_HOSTS';
 
 commit;