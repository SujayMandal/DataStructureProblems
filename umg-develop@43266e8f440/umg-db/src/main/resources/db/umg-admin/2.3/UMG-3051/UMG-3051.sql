use umg_admin;

create table USERS_LOGIN_AUDIT(username varchar(50) not null,
tenant_code varchar(45) not null,
sys_ip_address varchar(15) not null,
access_on bigint(20) not null,
activity varchar(126) not null,
reason_code varchar(32) not null,
PRIMARY KEY (access_on),
CONSTRAINT FK_USERS_LOGIN_AUDIT_KEY FOREIGN KEY (username, tenant_code) REFERENCES USERS (username, tenant_code) ON DELETE NO ACTION ON UPDATE NO ACTION);