use umg_admin;

Delete FROM `SYSTEM_PARAMETER` where SYS_KEY = 'RA_API_RECORD_LIMIT_TENANT_OUT_ONLY';

commit;