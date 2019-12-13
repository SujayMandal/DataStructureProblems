use umg_admin;

Delete FROM `umg_admin`.`SYSTEM_PARAMETER` where SYS_KEY = 'STRINGS_AS_FACTORS';

commit;