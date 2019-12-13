use umg_admin;

alter table USER_ROLES drop index UNQ_USER_PER_ROLE_PER_TENANT;
alter table USER_ROLES drop column TENANT_CODE;

alter table USER_ROLES add column USER_ROLE_ID INT(11);
alter table USER_ROLES add UNIQUE KEY UNI_USERNAME_ROLE (ROLE,USERNAME);

drop table if exists USERS_LOGIN_AUDIT;

alter table USERS drop column TENANT_CODE;
alter table USERS drop index unq_user_per_tennant;


alter table USERS drop column NAME,
drop column OFFICIAL_EMAIL,
drop column ORGANIZATION,
drop column COMMENTS,
drop column CREATED_ON,
drop column LAST_ACTIVATED_ON,
drop column LAST_DEACTIVATED_ON;

drop table if exists COMMAND;

DROP TABLE IF EXISTS POOL_USAGE_ORDER;
DROP TABLE IF EXISTS POOL_CRITERIA_DEF_MAPPING;
DROP TABLE IF EXISTS POOL;
DROP TABLE IF EXISTS POOL_CRITERIA;

COMMMIT;

