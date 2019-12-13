use umg_admin;

alter table USERS 
add column NAME varchar(126) comment 'Name of User',
add column OFFICIAL_EMAIL varchar(252) comment 'Official E-mail ID of User',
add column ORGANIZATION varchar(126) comment 'Organization of User',
add column COMMENTS varchar(252) comment 'Comment of User',
add column CREATED_ON bigint(20) comment 'Created Date in milliseconds in GMT',
add column LAST_ACTIVATED_ON bigint(20) comment 'Last Activated Date of this User in milliseconds in GMT',
add column LAST_DEACTIVATED_ON bigint(20) comment 'Last Deactivated Date of this User in milliseconds in GMT';