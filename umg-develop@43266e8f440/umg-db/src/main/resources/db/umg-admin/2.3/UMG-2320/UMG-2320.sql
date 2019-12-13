# Added new column tenant_code for USERS table

use umg_admin;
alter table USERS add column TENANT_CODE varchar(45) not null default 'localhost' comment 'Tenant Code';
alter table USERS add constraint unq_user_per_tennant unique (username, TENANT_CODE);
commit;

# Added new column tenant_code for USER_ROLES table

use umg_admin;
alter table USER_ROLES drop column user_role_id;
alter table USER_ROLES drop index uni_username_role;
alter table USER_ROLES add column TENANT_CODE varchar(45) not null default 'localhost' comment 'Tenant Code';
alter table USER_ROLES add constraint unq_user_per_role_per_tenant unique (username, ROLE, TENANT_CODE);
commit;