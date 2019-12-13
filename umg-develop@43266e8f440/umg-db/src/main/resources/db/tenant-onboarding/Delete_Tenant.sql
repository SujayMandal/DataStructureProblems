use umg_admin;

DELETE FROM NOTIFICATION_EVENT_TEMPLATE_MAPPING WHERE TENANT_ID NOT IN ('demo','hubzu','automation','denverr');

DELETE FROM TENANT_USER_TENANT_ROLE_MAPPING WHERE tenant_role_map_id IN (SELECT ID FROM TENANT_ROLES_MAPPING WHERE tenant_code NOT IN ('demo','hubzu','automation','denverr'));

DELETE FROM TENANT_USER_MAPPING WHERE tenant_code NOT IN ('demo','hubzu','automation','denverr');

Delete from TENANT_CONFIG WHERE TENANT_ID in (SELECT ID FROM TENANT WHERE CODE NOT IN ('demo','hubzu','automation','denverr') );

DELETE FROM ADDRESS WHERE TENANT_ID in (SELECT ID FROM TENANT WHERE CODE NOT IN ('demo','hubzu','automation','denverr') );

DELETE FROM AUTHTOKEN WHERE TENANT_ID in (SELECT ID FROM TENANT WHERE CODE NOT IN ('demo','hubzu','automation','denverr') );

DELETE FROM PERMISSION_ROLES_MAPPING WHERE tenant_roles_map_id in (SELECT ID FROM TENANT_ROLES_MAPPING WHERE tenant_code NOT IN ('demo','hubzu','automation','denverr') )

DELETE FROM TENANT_ROLES_MAPPING WHERE tenant_code NOT IN ('demo','hubzu','automation','denverr') ;

DELETE FROM TENANT WHERE CODE NOT IN ('demo','hubzu','automation','denverr');