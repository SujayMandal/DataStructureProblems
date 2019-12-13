use umg_admin;

-- execute the backed up create/insert scripts of tables "USERS, USER_ROLES, USERS_LOGIN_AUDIT" which was taken prior to role-based-scripts

DROP TABLE if exists `PERMISSION_ROLES_MAPPING`;

DROP TABLE if exists `PERMISSIONS`;

DROP TABLE if exists `TENANT_USER_TENANT_ROLE_MAPPING`;

DROP TABLE if exists  `TENANT_ROLES_MAPPING`;

DROP TABLE if exists  `TENANT_USER_MAPPING`;

DROP TABLE if exists `ROLES`;

DROP TABLE if exists `USERS_LOGIN_AUDIT`;

DROP TABLE if exists `USERS`;

commit;
