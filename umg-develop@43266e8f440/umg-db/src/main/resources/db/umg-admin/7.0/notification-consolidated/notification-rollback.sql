use umg_admin;


DELETE FROM SYSTEM_PARAMETER WHERE SYS_KEY in ('SMTP_HOST_NAME', 'SMTP_SERVER', 'NOTIFICATION_TEMPLATE_DIR', 'UMG_ADMIN_URL', 'FROM_ADDRESS', 'fromAddress', 'toAddress');

DELETE FROM SYSTEM_PARAMETER_AUDIT WHERE SYS_KEY in ('SMTP_HOST_NAME', 'SMTP_SERVER', 'NOTIFICATION_TEMPLATE_DIR', 'UMG_ADMIN_URL', 'FROM_ADDRESS', 'fromAddress', 'toAddress');


DROP TABLE IF EXISTS NOTIFICATION_EVENT_TEMPLATE_MAPPING;

DROP TABLE IF EXISTS NOTIFICATION_EMAIL_TEMPLATE;

DROP TABLE IF EXISTS NOTIFICATION_SMS_TEMPLATE;

DROP TABLE IF EXISTS NOTIFICATION_EVENT;

DROP TABLE IF EXISTS NOTIFICATION_TYPE;



	
COMMIT;