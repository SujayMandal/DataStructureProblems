USE `umg_admin`;

SELECT @tenant_equator:=(SELECT ID FROM TENANT WHERE CODE='equator');
DELETE FROM TENANT_CONFIG WHERE TENANT_ID = @tenant_equator;
DELETE FROM ADDRESS where TENANT_ID = @tenant_equator;
DELETE FROM TENANT where ID = @tenant_equator; 
COMMIT;